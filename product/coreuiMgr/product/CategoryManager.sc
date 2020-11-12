@Sync
class CategoryManager extends BaseProductManager {
   @Sync(initDefault=true)
   List<Category> categoryList;

   @Sync(initDefault=true)
   Category parentCategory;

   @Sync(resetState=true, initDefault=true)
   String findProductsText;

   @Sync(initDefault=true)
   List<Product> matchingProducts;

   @Sync(resetState=true, initDefault=true)
   String categoryPathName;

   @Sync(initDefault=true)
   Product selectedProduct;
   boolean productAddValid = false;
   String addProductStatus;
   String addProductError;

   @Sync(initDefault=true)
   String productQueryText;
   @Sync(initDefault=true)
   List<String> productQuerySuggestions;
   String productQueryStatus;
   String productQueryError;

   void updateElementInstance(ManagedElement el) {
      category = (Category) el;
      if (el == null)
         categoryPathName = null;
      // Don't set this for unsaved products since it reflects in the location bar
      else if (category.id != 0 && category.pathName != null && category.pathName.length() > 0)
         categoryPathName = category.pathName;
   }

   void resetForm() {
      super.resetForm();
      categoryList = null;
      element = null;
      clearFormErrors();
      categoryPathName = null;
   }

   void clearFormErrors() {
      super.clearFormErrors();
   }

   String getElementType() {
      return "category";
   }

   void updateCategoryPathName(String pathName) {
      super.updateCategoryPathName(pathName);
      categoryPathName = pathName;
   }

   void updateCategoryName(String val) {
      super.updateCategoryName(val);
      if (category == null)
         categoryPathName = null;
      else
         categoryPathName = category.pathName;
   }
}
