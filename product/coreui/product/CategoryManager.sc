class CategoryManager extends BaseProductManager {
   List<Category> categoryList;

   @Sync(resetState=true, initDefault=true)
   Category parentCategory;

   void updateElementInstance(CatalogElement el) {
      category = (Category) el;
   }

   void resetForm() {
      super.resetForm();
      categoryList = null;
      element = null;
      clearFormErrors();
   }

   void clearFormErrors() {
      super.clearFormErrors();
   }

   String getElementType() {
      return "category";
   }
}
