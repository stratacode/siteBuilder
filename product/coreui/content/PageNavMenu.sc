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
      visible := storeView != null;
      name := "cart(" + storeView.orderView.numLineItems + ")";
      icon = "/icons/shopping-cart20.svg";
      url := storeView.checkoutUrl;

      orderValue = 10;
   }
}
