class ProductManager extends BaseManager {
   List<Product> productList;

   @Sync(resetState=true, initDefault=true)
   Product product;

   void updateElementInstance(CatalogElement el) {
      product = (Product) el;
   }

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

   List<Sku> matchingSkus;

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
      super.resetForm();
      productList = null;
      element = null;
      productSaved = false;
      clearFormErrors();
      parentCategoryPathName = "";
   }

   void clearFormErrors() {
      super.clearFormErrors();
      statusMessage = null;
      errorMessage = null;

      skuAddErrorMessage = null;
      skuFindErrorMessage = null;
      skuStatusMessage = null;

      optionErrorMessage = null;
      optionStatusMessage = null;
   }

   String statusMessage;
   String errorMessage;
   String skuFindErrorMessage;
   String skuAddErrorMessage;
   String skuStatusMessage;

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

   String getElementType() {
      return "product";
   }
}
