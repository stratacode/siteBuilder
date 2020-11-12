class BlogCategoryView extends BlogElementView {
   @Sync(initDefault=true)
   BlogCategory category;

   // There is a catalogElement property here but it's not bindable so need to put this in each subclass
   elementMedia := category.altMedia;
   elementMainMedia := category.mainMedia;
   elementMediaChangedCt := category.mediaChangedCt;

   category =: validateCategory();

   @Sync
   String categoryViewError;

   ManagedElement getManagedElement() {
      return category;
   }

   void validateCategory() {
      validateCurrentMedia();
   }
}
