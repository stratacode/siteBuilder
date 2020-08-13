import java.util.Arrays;

@DBTypeSettings
class CountryInfo {
   @FindBy @DBPropertySettings(indexed=true, required=true)
   String countryName;

   @FindBy @DBPropertySettings(indexed=true, required=true)
   String countryCode;

   int postalCodeLen;

   Date lastModified;

   // The original countries.csv file has only countryName and countryCode - we compute postalCodeLen from the postal_code_info
   static List<String> csvProperties = Arrays.asList("countryName", "countryCode", "postalCodeLen");
}
