class CategoryManager extends BaseProductManager {
   List<Category> categoryList;

   @Sync(resetState=true, initDefault=true)
   Category parentCategory;

   String findProductsText;
   List<Product> matchingProducts;
   Product selectedProduct;
   boolean productAddValid = false;
   String addProductStatus;
   String addProductError;

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
