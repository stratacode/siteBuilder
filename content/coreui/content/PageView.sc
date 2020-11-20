@Sync(onDemand=true)
@CompilerSettings(constructorProperties="siteView,pagePathName")
@Component
class PageView extends ParentView {
   String pagePathName;
   PageDef pageDef;
   PageType pageType;

   SiteView siteView;

   String errorMessage = null;

   List<ViewDef> childViewDefs := pageDef.childViewDefs;

   PageView(SiteView siteView, PageDef pd, String pathName) {
      this.siteView = siteView;
      pagePathName =  pathName;
   }

   ViewDef getViewDef() {
      return pageDef;
   }
   void setViewDef(ViewDef viewDef) {
      pageDef = (PageDef) viewDef;
   }
}