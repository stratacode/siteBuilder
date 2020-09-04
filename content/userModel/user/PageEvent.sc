class PageEvent extends SessionEvent {
   String pathName;

   String getEventName() {
      return "page view";
   }

   String getEventDetail() {
      return pathName;
   }

   String getEventTarget(SiteContext site) {
      return "/" + site.sitePathTypeName + "/page/" + pathName;
   }
}
