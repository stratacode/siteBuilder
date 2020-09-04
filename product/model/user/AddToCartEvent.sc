class AddToCartEvent extends SessionEvent {
   String productPathName;
   AddToCartEvent(Product product) {
      productPathName = product.pathName;
   }
   AddToCartEvent() {
   }

   String getEventName() {
      return "add to cart";
   }

   String getEventDetail() {
      return productPathName;
   }

   String getEventTarget(SiteContext site) {
      return "/" + site.sitePathTypeName + "/page/" + productPathName;
   }
}
