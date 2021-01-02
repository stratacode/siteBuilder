import java.util.Arrays;

@DBTypeSettings
class CityInfo {
   @IdSettings(definedByDB=false)
   int geoNameId;

   @DBPropertySettings(indexed=true, required=true)
   String cityName;

   @DBPropertySettings(required=true)
   String countryCode;

   long population;
   int elevation; // in meters

   double latitude, longitude;

   String timezone; // iana timezone id

   Date modificationDate;

   // The original countries.csv file has only countryName and countryCode - we compute postalCodeLen from the postal_code_info
   static List<String> csvProperties = 
       Arrays.asList("geoNameId", "cityName", null /* ascii name */, null /* alternate names */, "latitude", "longitude",
                     null /* feature class */, null /* feature code */, "countryCode", null /* cc2 */, null /* admin1 */, null, null, null /* admin4 */,
                     "population",  "elevation", null, "timezone", "modificationDate" /* mod date - yyyy-MM-dd */);
}
