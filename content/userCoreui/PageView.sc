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
         errorMessage = "No page found with path name: " + pathName;
         return;
      }

      pageType = PageManager.findPageType(pageDef.pageTypePathName);
      if (pageType == null)
         errorMessage = "No page type found: " + pageDef.pageType;
   }
}