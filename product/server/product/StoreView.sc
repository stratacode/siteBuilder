StoreView {
   void validateSite() {
      super.validateSite();
      if (siteContext instanceof Storefront) {
         if (store != siteContext) {
            store = (Storefront) siteContext;
            orderView = new OrderView(this, currentUserView);
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
      super.pageVisited();
      if (orderView != null)
         orderView.pageVisited();
   }

   UserSession getUserSession() {
      return orderView.userView.getUserSession(store);
   }

}
