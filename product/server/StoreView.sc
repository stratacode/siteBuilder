StoreView {
   void init() {
      Storefront storefront = Storefront.findByStorePathName(pathName);
      if (storefront == null) {
         System.err.println("*** No storefront with pathname: " + pathName);
      }
      else
         store = storefront;
   }

}