@DBTypeSettings(typeId=1)
class PageDef extends ParentDef {
   SiteContext site;
   String pageName;

   @FindBy(with="site", orderBy="-lastModified")
   String pagePathName; // e.g. about, featured-products -

   String pageTypePathName; // e.g. stores, blogs, sites etc - determines the URL and the template class used to render the page

   transient PageType pageType;

   boolean visible = true;

   @FindBy(with="site", orderBy="-lastModified")
   boolean homePage = false;

   // Should this page be available as a template for new pagres
   boolean useAsTemplate;

   Date lastModified;

   String pageUrl := "/" + site.sitePathTypeName + "/" + site.sitePathName + "/page/" + pagePathName;

   String validatePageName(String pageName) {
      return IPropValidator.validateRequired("page name", pageName);
   }

   String validatePagePathName(String pagePathName) {
      return ManagedResource.validatePathName("page path name", pagePathName);
   }
}
