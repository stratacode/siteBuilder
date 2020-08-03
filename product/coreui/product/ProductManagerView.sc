@Component
@Sync
class ProductManagerView {
   Storefront store;

   @Sync(resetState=true, initDefault=true)
   String searchText;

   List<Product> productList;

   @Sync(resetState=true, initDefault=true)
   Product product;

   /**
    * Save the un-validated html in a temporary property so that it gets sync'd properly for session reset
    * When this changes, a binding triggers updateLongDesc to do the validation and update product.longDesc
    */
   @Sync(resetState=true, initDefault=true)
   String longDescHtml;

   int skuTypeId = 1; // 1 = Sku, 2 = PhysicalSku using dbTypeId annotation values
   Sku sku;
   PhysicalSku psku;

   @Sync(resetState=true, initDefault=true)
   boolean addInProgress;
   @Sync(resetState=true, initDefault=true)
   boolean addSkuInProgress;
   @Sync(resetState=true, initDefault=true)
   boolean showSkuView;
   @Sync(resetState=true, initDefault=true)
   boolean skuEditable;
   @Sync(resetState=true, initDefault=true)
   boolean showSkuOptions;
   boolean productSaved;

   @Sync(resetState=true, initDefault=true)
   Category defaultCategory;

   List<Sku> matchingSkus;

   List<Category> matchingCategories;

   List<ManagedMedia> matchingMedia;

   List<OptionScheme> matchingOptionSchemes;

   @Sync(resetState=true, initDefault=true)
   boolean showOptionsView = false;
   @Sync(resetState=true, initDefault=true)
   boolean showOptionSchemeView = false;
   @Sync(resetState=true, initDefault=true)
   boolean editableOptionScheme = false;

   @Sync(resetState=true, initDefault=true)
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

   String findMediaText;
   String mediaStatusMessage;
   String mediaErrorMessage;
   boolean uploadInProgress = false;

   boolean optionSchemeSaved = false;
   String optionStatusMessage;
   String optionErrorMessage;

   boolean autoUpdatePath = false;

   void updateProductName(String val) {
      if (product == null)
         return;
      product.name = val;
      if (autoUpdatePath && (val != null && val.length() > 0))
         product.pathName = CatalogElement.convertToPathName(val);
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
