@DBTypeSettings(typeId=1)
class PageDef extends ParentDef {
   String pageName;
   String pagePathName;

   // Should this page be available as a template for new pagres
   boolean pageTemplate;

   // Ability to override the default css theme here?
}
