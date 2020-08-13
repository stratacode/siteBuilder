SiteView {
   pathName =: validateSite();

   void start() {
      validateSite();
   }

   void validateSite() {
      SiteContext ctx = SiteContext.findBySitePathName(pathName);
      if (ctx == null) {
         System.err.println("*** No site with pathname: " + pathName);
      }
      else
         this.siteContext = ctx;
   }

   void pageVisited() {
   }
}
