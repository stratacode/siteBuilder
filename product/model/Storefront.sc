/** The top-level model object for an online store */
@DBTypeSettings
class Storefront {
   public final static int MaxQuantity = 100000;
   String storeName;
   @FindBy(findOne=true)
   String storePathName;
   String defaultCurrencyName = "USD";

   String icon;

   Currency getDefaultCurrency() {
      return Currency.currencyForName.get(defaultCurrencyName);
   }

   List<Currency> supportedCurrencies;

   public boolean supportsCurrency(Currency cur) {
      return cur == defaultCurrency || (supportedCurrencies != null && supportedCurrencies.contains(cur));
   }
}
