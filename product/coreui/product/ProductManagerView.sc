@Component
@Sync
class ProductManagerView {
   Storefront store;

   String searchText; 

   List<Product> productList;

   Product product;

   Sku sku;

   boolean addInProgress;
   boolean addSkuInProgress;

   Category defaultCategory;

   List<Sku> matchingSkus;

   List<Category> matchingCategories;

   void resetForm() {
      searchText = "";
      productList = null;
      product = null;
      errorMessage = null;
      defaultCategory = null;
   }

   String statusMessage;
   String errorMessage;
   String skuErrorMessage;

   boolean autoUpdatePath = false;

   void updateProductName(String val) {
      if (product == null)
         return;
      product.name = val;
      if (!autoUpdatePath || (val == null || val.length() == 0))
         return;
      product.pathName = product.convertToPathName(val);
   }

   void updatePathName(String pathName) {
      if (product == null)
         return;
      autoUpdatePath = false;
      product.pathName = pathName;
   }

}
