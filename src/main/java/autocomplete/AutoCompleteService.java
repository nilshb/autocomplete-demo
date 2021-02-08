package autocomplete;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

import static org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder.SUGGESTION_NAME;

@Service
public class AutoCompleteService {

    private static final Logger log = LoggerFactory.getLogger(AutoCompleteService.class);

    private static final String FIELD_COMPLETION = "address.completion";
    private static final String INDEX = "addresses";

    private final RestHighLevelClient client;

    @Autowired
    public AutoCompleteService(RestHighLevelClient client) {
        this.client = client;
    }

    public Completions autocomplete(String prefixString, int size) {
        SearchRequest searchRequest = new SearchRequest(INDEX);
        CompletionSuggestionBuilder suggestBuilder = new CompletionSuggestionBuilder(FIELD_COMPLETION);

        suggestBuilder.size(size)
                .prefix(prefixString, Fuzziness.ONE)
                .skipDuplicates(true)
                .analyzer("standard");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.suggest(new SuggestBuilder().addSuggestion(SUGGESTION_NAME, suggestBuilder));
        searchRequest.source(sourceBuilder);

        SearchResponse response;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
            return findCompletions(response);
        } catch (IOException ex) {
            log.error("Error in autocomplete search", ex);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Error in ES search");
        }
    }

    private Completions findCompletions(SearchResponse response) {
        Completions completions = new Completions();
        Suggest suggest = response.getSuggest();
        Suggest.Suggestion<Suggest.Suggestion.Entry<Suggest.Suggestion.Entry.Option>> suggestion = suggest.getSuggestion(SUGGESTION_NAME);
        for (Suggest.Suggestion.Entry<Suggest.Suggestion.Entry.Option> entry : suggestion.getEntries()) {
            for (Suggest.Suggestion.Entry.Option option : entry.getOptions()) {
                completions.addresses.add(option.getText().toString());
            }
        }

        return completions;
    }


}
