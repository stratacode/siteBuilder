StoreView {
   void validateSite() {
      super.validateSite();
      if (siteContext instanceof Storefront) {
         store = (Storefront) siteContext;
         orderView = new OrderView(store);
      }
      else {
         System.err.println("*** validateSite - not a valid Storefront");
         store = null;
         orderView = null;
      }
   }
}
