@Component
@Sync
class ProductManagerView {
   Storefront store;

   @Sync(resetState=true)
   String searchText;

   List<Product> productList;

   @Sync(resetState=true)
   Product product;

   int skuTypeId = 1; // 1 = Sku, 2 = PhysicalSku using dbTypeId annotation values
   Sku sku;
   PhysicalSku psku;

   @Sync(resetState=true)
   boolean addInProgress;
   @Sync(resetState=true)
   boolean addSkuInProgress;
   @Sync(resetState=true)
   boolean showSkuView;
   @Sync(resetState=true)
   boolean skuEditable;
   @Sync(resetState=true)
   boolean showSkuOptions;
   boolean productSaved;

   Category defaultCategory;

   List<Sku> matchingSkus;

   List<Category> matchingCategories;

   List<ManagedMedia> matchingMedia;

   List<OptionScheme> matchingOptionSchemes;

   boolean showOptionsView = false;
   boolean showOptionSchemeView = false;
   boolean editableOptionScheme = false;

   OptionScheme optionScheme = null;

   // Used for focusing
   ProductOption newProductOption;
   OptionValue newOptionValue;

   List<String> optionMediaFilter = null;

   List<Sku> missingSkuOptions;
   List<Sku> validSkuOptions;
   List<Sku> invalidSkuOptions;

   void resetForm() {
      searchText = "";
      productList = null;
      product = null;
      defaultCategory = null;
      productSaved = false;
      clearFormErrors();
   }

   void clearFormErrors() {
      statusMessage = null;
      errorMessage = null;

      skuAddErrorMessage = null;
      skuFindErrorMessage = null;
      skuStatusMessage = null;

      uploadInProgress = false;
      clearMediaErrors();
      optionErrorMessage = null;
      optionStatusMessage = null;
   }

   void clearMediaErrors() {
      mediaStatusMessage = null;
      mediaErrorMessage = null;
   }

   String statusMessage;
   String errorMessage;
   String skuFindErrorMessage;
   String skuAddErrorMessage;
   String skuStatusMessage;

   String mediaStatusMessage;
   String mediaErrorMessage;
   boolean uploadInProgress = false;

   boolean autoUpdatePath = false;

   boolean optionSchemeSaved = false;
   String optionStatusMessage;
   String optionErrorMessage;

   void updateProductName(String val) {
      if (product == null)
         return;
      product.name = val;
      if (autoUpdatePath && (val != null && val.length() > 0))
         product.pathName = product.convertToPathName(val);
      if (productSaved)
         product.validateProperties();
      else
         product.validateProp("name");
   }

   void updatePathName(String pathName) {
      if (product == null)
         return;
      autoUpdatePath = false;
      product.pathName = pathName;
   }

}
