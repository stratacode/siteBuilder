@DBTypeSettings(typeId=1)
class PageDef extends ParentDef {
   String pageName;
   String pagePathName;

   String pageType; // store, blog, plain etc - determines the URL and the template class used to render the page

   // Should this page be available as a template for new pagres
   boolean pageTemplate;

   // Ability to override the default css theme here?
}
