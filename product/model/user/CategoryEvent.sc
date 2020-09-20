@Sync(onDemand=true)
class CategoryEvent extends SessionEvent {
   String categoryPathName;
   CategoryEvent(Category c) {
      categoryPathName = c.pathName;
   }
   CategoryEvent() { // for json serialization
   }

   String getEventName() {
      return "category view";
   }

   String getEventDetail() {
      return categoryPathName;
   }

   String getEventTarget(SiteContext site) {
      return "/" + site.sitePathTypeName + "/" + site.sitePathName + "/category/" + categoryPathName;
   }
}
