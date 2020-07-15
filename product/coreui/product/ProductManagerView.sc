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

   List<ManagedMedia> matchingMedia;

   void resetForm() {
      searchText = "";
      productList = null;
      product = null;
      defaultCategory = null;
      clearFormErrors();
   }

   void clearFormErrors() {
      statusMessage = null;
      errorMessage = null;
      skuErrorMessage = null;
      uploadInProgress = false;
      clearMediaErrors();
   }

   void clearMediaErrors() {
      mediaStatusMessage = null;
      mediaErrorMessage = null;
   }

   String statusMessage;
   String errorMessage;
   String skuErrorMessage;

   String mediaStatusMessage;
   String mediaErrorMessage;
   boolean uploadInProgress = false;

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
