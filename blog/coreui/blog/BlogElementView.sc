@CompilerSettings(constructorProperties="siteView,pathName")
abstract class BlogElementView extends ManagedElementView {
   @Sync(initDefault=true)
   List<BlogCategory> categoryPath;

   ViewDef viewDef;

   BlogElement getBlogElement() {
      return (BlogElement) getManagedElement();
   }

   void setViewDef(ViewDef viewDef) {
      this.viewDef = viewDef;
   }

   ViewDef getViewDef() {
      return viewDef;
   }
}
