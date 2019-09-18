# rAPIo
Intergration to Hedvig

## Price comparison

Create comparison: `https:/xxxx/v1/quote`
Sign offer: `https://xxxx/v1/quote/{quote_id}/sign`

### Create request

  * request_id `String`
  * product_type `String`='HOME'
  * quote_data 
    * personal_number `String`
    * street `String`
    * zipcode `Int`
    * city `String`
    * livingspace (m2) `Int`
    * household_size (people to insure) `Int`
    * include_brf_coverage `Boolean`
    * is_student `Boolean`
    
    * phone_number `String`


#### Response
  * request_id `String`
  * quote_id `String`
  * price_per_month
    * amount `String` ex '295.00'
    * currency `String`
  * valid_to `Integer` unix timestamp

### Sign request

  * request_id `String`
  * quote_id `String`
  * starts_at: 
    * date `YYYY-MM-DD`
    * timezone `String` eg 'Europe/Stockholm'
  * email `String`
  * phone_number `String`

#### Response
  * request_id `String`
  * offer_id `String`
  * signed_at `Int`
