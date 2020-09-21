@Sync(onDemand=true)
@CompilerSettings(constructorProperties="siteView,pagePathName")
class PageView {
   String pagePathName;
   PageDef pageDef;
   PageType pageType;

   SiteView siteView;

   String errorMessage = null;

   List<IView> childViews;

   PageView(SiteView siteView, PageDef pd, String pathName) {
      this.siteView = siteView;
      pagePathName =  pathName;
   }

}