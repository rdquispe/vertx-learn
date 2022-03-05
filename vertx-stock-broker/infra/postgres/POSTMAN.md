### Put

```
curl --location --request PUT 'localhost:8888/pg/account/watchlist/101' \
--header 'Content-Type: application/json' \
--data-raw '{
    "assets": [
        {
            "name": "MSFT"
        },
        {
            "name": "AMNZ"
        }
    ]
}'
```


### Delete

```
curl --location --request DELETE 'localhost:8888/pg/account/watchlist/101'
```
