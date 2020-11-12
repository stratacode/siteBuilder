abstract class CatalogElementView extends ManagedElementView {
   StoreView storeView := (StoreView) siteView;

   ViewDef viewDef;

   @Sync(initDefault=true)
   List<Category> categoryPath;

   CatalogElement getCatalogElement() {
      return (CatalogElement) getManagedElement();
   }

   void setViewDef(ViewDef viewDef) {
      this.viewDef = viewDef;
   }

   ViewDef getViewDef() {
      return viewDef;
   }
}
