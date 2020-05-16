@Component
object StoreView {
   Storefront store;

   String storePathName;

   void init() {
      List<Storefront> storefronts = Storefront.findByStorePathName(storePathName);
      if (storefronts.size() == 0) {
         System.err.println("*** No storefront with pathname: " + storePathName);
         store = new Storefront();
         store.defaultCurrency = Currency.usDollars;
      }
      else
         store = storefronts.get(0);
   }

}
