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
