# rAPIo
Partner integration API to Hedvig for price comparison and siging offers.

API specification is found [here](https://docs.google.com/document/d/1x5cetC_JhJWJH4_TdHCxK9FSVa4MiItUkucQL1Z2ZkQ/edit#)

## Testing

### Local testing

The unit tests should work out of the box.

To manually test rAPIo locally:

- Start `RapioApplication` with:
    - Environment variables: POSTGRES_USERNAME=\<user\>;POSTGRES_PASSWORD=\<pass\>
    - Activ profiles: development, noauth, runtime, fakes, staging
  
- Create and sign a Swedish Apartment quote: 
  ```bash
  curl -0 -X POST http://localhost:5849/v1/quotes \
  -H 'Content-Type: application/json; charset=utf-8' \
  --data-binary @- << EOF
  {
    "requestId": "apa",
    "productType": "SWEDISH_APARTMENT",
    "quoteData":{
      "street": "apa",
      "zipCode": "123456",
      "city": "apa",
      "livingSpace": 123,
      "personalNumber": "199802262387",
      "householdSize": 2,
      "coInsured":2,
      "productSubType": "BRF"
    }
  }
  EOF

  curl -0 -X POST http://localhost:5849/v1/quotes/<quoteId>/sign \
  -H 'Content-Type: application/json; charset=utf-8' \
  --data-binary @- << EOF
  {
    "requestId": "apa", 
    "startsAt": {
      "date": "2021-02-20", 
      "timezone": "Europe/Stockholm"}, 
      "email": "apa@apa.se", 
      "firstName": "Apa", 
      "lastName": "Apa"
    }
  }
  EOF

- Create and sign a Norwegian Travel quote:
  ```bash
  curl -0 -X POST http://localhost:5849/v1/quotes \
  -H 'Content-Type: application/json; charset=utf-8' \
  --data-binary @- << EOF
  {
    "requestId": "a", 
    "productType": "NORWEGIAN_TRAVEL", 
    "quoteData":{
      "birthDate": "1000-11-11", 
      "coInsured":0, 
      "youth": false 
    }
  }
  EOF

  curl -0 -X POST http://localhost:5849/v1/quotes/<quoteId>/sign \
  -H 'Content-Type: application/json; charset=utf-8' \
  --data-binary @- << EOF
  {
    "requestId": "apa", 
    "startsAt": {
      "date": "2021-02-20", 
      "timezone": "Europe/Stockholm"}, 
      "email": "apa@apa.se", 
      "firstName": "Apa", 
      "lastName": "Apa",
      "personalNumber": "12121212345"
    }
  }
  EOF
  
- Create and sign a Norwegian Home Content quote:
  ```bash
  curl -0 -X POST http://localhost:5849/v1/quotes \
  -H 'Content-Type: application/json; charset=utf-8' \
  --data-binary @- << EOF
  {
    "requestId": "apa", 
    "productType": "NORWEGIAN_HOME_CONTENT", 
    "quoteData":{
      "street": "ApGatan", 
      "zipCode": "1234", 
      "city": "ApCity", 
      "birthDate": "1988-01-01", 
      "livingSpace": 122, 
      "coInsured":0, 
      "youth": false, 
      "productSubType": "OWN" 
    }
  }
  EOF

  curl -0 -X POST http://localhost:5849/v1/quotes/<quoteId>/sign \
  -H 'Content-Type: application/json; charset=utf-8' \
  --data-binary @- << EOF
  {
    "requestId": "apa", 
    "startsAt": {
      "date": "2021-02-20", 
      "timezone": "Europe/Stockholm"}, 
      "email": "apa@apa.se", 
      "firstName": "Apa", 
      "lastName": "Apa",
      "personalNumber": "12121212345"
    }
  }
  EOF

## Testing in Stage

Add basic auth and change host in the curls above:

- add `-u hedvigtest123:` to the curl
- change host and port to `https://extapi.dev.hedvigit.com/v1/quotes` in the curls above
