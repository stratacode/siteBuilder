import sc.user.currentUserView;
import sc.product.currentOrderView;
import sc.product.StoreView;

currentNavMenu {
   homeItem {
      name := StoreView.store.storeName;
      icon := StoreView.store.icon;
      url := StoreView.store.storePathName;
   }
   object cartMenu extends NavMenuItem {
      name := "cart(" + currentOrderView.numLineItems + ")";
      icon = "/icons/shopping-cart.svg";
      url = "/cart";
   }
}
