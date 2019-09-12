# rAPIo
Intergration to Hedvig

## Price comparison

Create comparison: `https:/xxxx/v1/comparison`
Sign offer: `https://xxxx/v1/comparison/offer_id/sign`

### Create request

  * request_id `String`
  * comparison_type `String`='HOME'
  * comparison_data 
    * personal_number `String`
    * street `String`
    * zipcode `Int`
    * city `String`
    * livingspace (m2) `Int`
    * household_size (people to insure) `Int`
    * brf `Boolean`
    
    * phone_number `String`

    * coverage_over_a_million `Boolean`
    * security_door `Boolean`
    * deductible `Int` 
    * all_risk `Boolean`
    * leisure_coverage `Boolean`
    * increased_travel_coverage `Boolean`
    * golf_insurance `Boolean`
    * alarm_connected_to_alarm_center `Boolean`
    * payment_interval `('MONTHLY', 'QUARTERLY',  'BI_ANNUAL' 'ANNUAL')`


#### Response
  * request_id `String`
  * offer_id `String`
  * price_per_month
    * amount `String` ex '295.00'
    * currency `String`
  * valid_to `Integer` unix timestamp

### Sign request

  * request_id `String`
  * offer_id `String`
  * start_date `YYYY-MM-DD`
  * timezone `String` eg 'Europe/Stockholm'
  * email `String`
  * phone_number `String`

#### Response
  * request_id `String`
  * offer_id `String`
  * signed_at `Int`
