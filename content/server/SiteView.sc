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
      pageVisitCount++;
   }

   PageView getPageView(String pathName) {
      List<PageDef> pageDefs;
      if (pathName == null)
         pageDefs = PageDef.findByHomePage(true, siteContext);
      else
         pageDefs = PageDef.findByPagePathName(pathName, siteContext);
      if (pageDefs == null || pageDefs.size() == 0)
         return new PageView(this, null, pathName);
      return new PageView(this, pageDefs.get(0), pathName);
   }
}
