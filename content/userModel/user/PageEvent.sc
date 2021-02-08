@Sync(onDemand=true)
class PageEvent extends WindowEvent {
   String pathName;

   @Sync(initDefault=true)
   /** 0 - not set. 1-100 % of the window that the user scrolled to */
   int scrollDepth;

   String getEventName() {
      return "page view";
   }

   String getEventDetail() {
      return pathName;
   }

   String getEventTarget(SiteContext site) {
      return "/" + site.sitePathTypeName + "/" + site.sitePathName + "/page/" + pathName;
   }

   void setScrollDepth(int val) {
      if (val == scrollDepth)
         return;
      scrollDepth = val;
      Bind.sendChangedEvent(this, "scrollDepthStr");
   }

   @Bindable(manual=true)
   public String getScrollDepthStr() {
      if (scrollDepth == 0) {
         return "?";
      }
      else
         return scrollDepth + "%";
   }
}
