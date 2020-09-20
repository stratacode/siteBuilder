@Sync(onDemand=true)
class ProductEvent extends SessionEvent {
   String productPathName;
   ProductEvent(Product p) {
      productPathName = p.pathName;
   }
   ProductEvent() {
   }

   String getEventName() {
      return "product view";
   }

   String getEventDetail() {
      return productPathName;
   }

   String getEventTarget(SiteContext site) {
      return "/" + site.sitePathTypeName + "/" + site.sitePathName + "/product/" + productPathName;
   }
}
