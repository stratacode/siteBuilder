@DBTypeSettings(typeId=1)
class PageDef extends ParentDef {
   SiteContext site;
   String pageName;
   String pagePathName;

   String pageType; // store, blog, plain etc - determines the URL and the template class used to render the page

   boolean visible;
   boolean homePage;

   // Should this page be available as a template for new pagres
   boolean useAsTemplate;
}
