@Sync(onDemand=true)
@DBTypeSettings(typeId=1)
@sc.js.JSSettings(dependentTypes="sc.content.SlideshowDef,sc.content.ContentViewDef")
@sc.obj.SyncTypeFilter(typeNames={"sc.content.SlideshowDef", "sc.content.ContentViewDef"})
class PageDef extends ParentDef {
   @Sync(initDefault=true)
   SiteContext site;
   @Sync(initDefault=true)
   String pageName;

   @Sync(initDefault=true)
   @FindBy(with="site", orderBy="-lastModified")
   String pagePathName; // e.g. about, featured-products -

   @Sync(initDefault=true)
   String pageTypePathName; // e.g. stores, blogs, sites etc - determines the URL and the template class used to render the page

   transient PageType pageType;

   boolean visible = true;

   @FindBy(with="site", orderBy="-lastModified")
   boolean homePage = false;

   // Should this page be available as a template for new pages
   @Sync(initDefault=true)
   boolean useAsTemplate;

   @Sync(initDefault=true)
   Date lastModified;

   String pageUrl := "/" + site.sitePathTypeName + "/" + site.sitePathName + "/page/" + pagePathName;

   String validatePageName(String pageName) {
      return IPropValidator.validateRequired("page name", pageName);
   }

   String validatePagePathName(String pagePathName) {
      return ManagedResource.validatePathName("page path name", pagePathName);
   }

   @sc.obj.ManualGetSet
   boolean equals(Object other) {
      if (!super.equals(other))
         return false;
      PageDef otherPage = (PageDef) other;
      if (!DynUtil.equalObjects(otherPage.pageName, pageName))
         return false;
      if (!DynUtil.equalObjects(otherPage.homePage, homePage))
         return false;
      if (!DynUtil.equalObjects(otherPage.useAsTemplate, useAsTemplate))
         return false;
      if (!DynUtil.equalObjects(otherPage.lastModified, lastModified))
         return false;
      return true;
   }

   @sc.obj.ManualGetSet
   int hashCode() {
      return pageName == null ? 0 : pageName.hashCode();
   }

}
