import java.util.Arrays;

@DBTypeSettings
class CountryInfo {
   @FindBy @DBPropertySettings(indexed=true, required=true)
   String countryName;

   @FindBy @DBPropertySettings(indexed=true, required=true)
   String countryCode;

   String capitalName;

   String iso3Code;

   int isoNum;

   /**
    * The length of the postalCode field in the postal_code_info table. For the US, this is just 5 and for CA it is just 3
    * This may not match postalCodeFormat or the regex.
    */
   int postalCodeLen;

   @FindBy(findOne=true) @DBPropertySettings(indexed=true)
   int geoNameId;

   Date lastModified;

   String currencyCode;

   String currencyName;

   String phone;

   String postalCodeFormat;

   String postalCodeRegex;

   String languages;

   String fips;

   String fipsEquiv;

   double area; // sq km

   int population; 

   String continent;

   String topLevelDomain;

   String neighbors;

   // The original countries.csv file has only countryName and countryCode - we compute postalCodeLen from the postal_code_info
   //static List<String> csvProperties = Arrays.asList("countryName", "countryCode", "postalCodeLen");
   static List<String> csvProperties = Arrays.asList("countryCode", "iso3Code", "isoNum", "fips", "countryName", "capitalName", "area", "population", "continent", "topLevelDomain", "currencyCode", "currencyName", "phone", "postalCodeFormat", "postalCodeRegex", "languages", "geoNameId", "neighbors", "fipsEquiv");

   /*
   int getPostalCodeLen() {
      if (postalCodeFormat == null)
         return 0;
      int ix = postalCodeFormat.indexOf('-');
      if (ix == -1)
         return postalCodeFormat.length();
      return ix;
   }
   */
}
