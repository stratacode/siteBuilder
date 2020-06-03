class CategoryView extends CatalogElementView {
   @Sync(initDefault=true)
   Category category;

   @Sync(initDefault=true)
   String categoryViewError;

   CatalogElement getCatalogElement() {
      return category;
   }

}
