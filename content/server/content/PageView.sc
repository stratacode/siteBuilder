PageView {
   pagePathName =: validatePageView();

   childViews := pageDef.createChildViews(this);

   void validatePageView() {
      List<PageDef> pageDefs;
      if (pagePathName == null)
         pageDefs = PageDef.findByHomePage(true, siteView.siteContext);
      else
         pageDefs = PageDef.findByPagePathName(pagePathName, siteView.siteContext);
      if (pageDefs == null || pageDefs.size() == 0) {
         SiteContext site = siteView.siteContext;
         if (site == null)
            errorMessage = "No site for path";
         else if (pagePathName == null)
            errorMessage = "No home page for site: " + siteView.siteContext.siteName;
         else
            errorMessage = "No page found with path name: " + pagePathName + " for site: " + siteView.siteContext.siteName;
         return;
      }

      pageDef = pageDefs.get(0);
      pageType = PageManager.findPageType(pageDef.pageTypePathName);
      if (pageType == null)
         errorMessage = "No page type found: " + pageDef.pageType;
   }
}
