class CategoryView extends CatalogElementView {
   @Sync(initDefault=true)
   Category category;

   @Sync(initDefault=true)
   String categoryViewError;

   ManagedElement getManagedElement() {
      return category;
   }

   category =: validateCategory();

   // There is a catalogElement property here but it's not bindable so need to put this in each subclass
   elementMedia := category.altMedia;
   elementMainMedia := category.mainMedia;

   void validateCategory() {
      validateCurrentMedia();
   }
}
