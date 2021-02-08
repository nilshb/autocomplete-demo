package autocomplete;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutoCompleteResource {

    private final AutoCompleteService service;

    public AutoCompleteResource(AutoCompleteService service) {
        this.service = service;
    }

    @GetMapping("/address")
    @CrossOrigin
    public Completions autocomplete(@RequestParam String q, @RequestParam(defaultValue = "20") int size) {
        return service.autocomplete(q, size);
    }
}
