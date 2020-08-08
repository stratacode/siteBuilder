PageNavMenu {
   StoreView storeView;

   void validateSiteView() {
      super.validateSiteView();
      if (siteView instanceof StoreView)
         storeView = (StoreView) siteView;
      else
         storeView = null;
   }

   object cartMenu extends NavMenuItem {
      name := "cart(" + storeView.orderView.numLineItems + ")";
      icon = "/icons/shopping-cart.svg";
      url = "/cart";
   }
}
