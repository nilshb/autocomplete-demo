Autocomplete on large dataset with elasticsearch
--
Springboot service using elasticsearch backend.

Here we will try to search for addresses. Hence index addresses and field address.

create index (addresses):
```
curl -XPUT --user <user>:<password> 'http://localhost:9200/addresses'
```

create mapping for address field:
```
curl -XPUT -H 'Content-Type: application/json' --user <user>:<passwors> 'http://localhost:9200/addresses/_mapping?pretty' -d '{
    "properties": {
      "address": {
        "type": "text",
        "fields": {
          "keyword": {
            "type": "keyword",
            "ignore_above": 256
          },
          "completion": {
            "type": "completion",
            "analyzer": "standard",
            "preserve_separators": true,
            "preserve_position_increments": true,
            "max_input_length": 100
          }
        }
    }   
   }
}'

```

then all you need is to populate index with documents containing an `address` field.

Enjoy!

