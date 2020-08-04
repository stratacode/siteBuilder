/** The top-level model object for an online store */
@DBTypeSettings
class Storefront implements IPropValidator {
   public final static int MaxQuantity = 100000;

   List<UserProfile> storeAdmins;

   // Inner class used without DBTypeSettings with FindBy. Generates a findBy method that will create
   // an instance of the inner class (and a prototype of the outer one if necessary), populate properties
   // from the findBy parameters and return the typed result
   //     List<Storefront> res = proto.storeForAdmin.findBy(currentUser, true);

   // @FindBy
   //class storeForAdmin {
   //   UserProfile currentUser;
   //   boolean userIsAdmin := storeAdmins.contains(currentUser);
   //}

   @Sync(resetState=true,initDefault=true)
   String storeName;

   @FindBy(findOne=true)
   @Sync(resetState=true,initDefault=true)
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

   @Bindable
   @DBPropertySettings(persist=false)
   Map<String,String> propErrors = null;

   static String validateStoreName(String storeName) {
      return IPropValidator.validateRequired("store name", storeName);
   }

   static String validateStorePathName(String storePathName) {
      return CatalogElement.validatePathName("store path name", storePathName);
   }
}
