/** The top-level model object for an online store */
@DBTypeSettings
class Storefront {
   public final static int MaxQuantity = 100000;
   String storeName;
   @FindBy(findOne=true)
   String storePathName;

   String icon;

   String orderPrefix = "sc";

   Currency getDefaultCurrency() {
      return Currency.currencyForName.get(defaultCurrencyName);
   }

   List<String> supportedCurrencies;
   String defaultCurrencyName = "USD";
   List<String> countryNames;
   String defaultCountry = "United States";

   public boolean supportsCurrency(Currency cur) {
      return cur == defaultCurrency || (supportedCurrencies != null && supportedCurrencies.contains(cur.currencyName));
   }

   MediaManager mediaManager;

   public String toString() {
      return storeName;
   }
}
