class PageView {
   String pagePathName;
   PageDef pageDef;
   PageType pageType;

   SiteView siteView;

   String errorMessage = null;

   PageView(SiteView siteView, PageDef pd, String pathName) {
      this.siteView = siteView;
      pageDef = pd;
      pagePathName =  pathName;
      if (pd == null) {
         if (pathName == null)
            errorMessage = "No home page for site: " + siteView.siteContext.siteName;
         else
            errorMessage = "No page found with path name: " + pathName + " for site: " + siteView.siteContext.siteName;
         return;
      }

      pageType = PageManager.findPageType(pageDef.pageTypePathName);
      if (pageType == null)
         errorMessage = "No page type found: " + pageDef.pageType;
   }
}