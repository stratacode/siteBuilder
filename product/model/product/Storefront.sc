/** The top-level model object for an online store */
@DBTypeSettings(typeId=2)
class Storefront extends SiteContext {
   public final static int MaxQuantity = 100000;

   String orderPrefix = "sc";

   Currency getDefaultCurrency() {
      return Currency.getCurrencyForName(defaultCurrencyName);
   }

   List<String> supportedCurrencies;
   String defaultCurrencyName = "USD";
   List<String> countryNames;
   String defaultCountry = "United States";

   public boolean supportsCurrency(Currency cur) {
      return cur == defaultCurrency || (supportedCurrencies != null && supportedCurrencies.contains(cur.currencyName));
   }

   String getSiteTypeName() {
      return "Store";
   }

   String getSitePathTypeName() {
      return "stores";
   }
}
