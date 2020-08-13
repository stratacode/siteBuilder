StoreView {
   void validateSite() {
      super.validateSite();
      if (siteContext instanceof Storefront) {
         if (store != siteContext) {
            store = (Storefront) siteContext;
            orderView = new OrderView(store, currentUserView);
         }
         else
            orderView.refresh();
      }
      else {
         System.err.println("*** validateSite - not a valid Storefront");
         store = null;
         orderView = null;
      }
   }

   void pageVisited() {
      if (orderView != null)
         orderView.refresh();
   }

}
