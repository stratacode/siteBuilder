import java.util.Arrays;

@DBTypeSettings
class CountryInfo {
   @FindBy @DBPropertySettings(indexed=true, required=true)
   String countryName;

   @FindBy @DBPropertySettings(indexed=true, required=true)
   String countryCode;

   int postalCodeLen;

   // TODO: add format of postal code for validation - the # of "index chars" - i.e. in the postalCodeDB and a list of valid code lengths

   Date lastModified;

   static List<String> csvProperties = Arrays.asList("countryName", "countryCode");
}
