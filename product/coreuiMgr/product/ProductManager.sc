@Sync
class ProductManager extends BaseProductManager {
   @Sync(initDefault=true)
   List<Product> productList;

   @Sync(resetState=true, initDefault=true)
   String skuSearchText;

   @Sync(resetState=true, initDefault=true)
   Product product;

   @Sync(resetState=true, initDefault=true)
   String productPathName;

   void updateElementInstance(CatalogElement el) {
      product = (Product) el;
      if (el == null)
         productPathName = null;
      // Don't set this for unsaved products since it reflects in the location bar
      else if (product.id != 0 && product.pathName != null && product.pathName.length() > 0)
         productPathName = product.pathName;
   }

   @Sync(resetState=true, initDefault=true)
   int skuTypeId = 1; // 1 = Sku, 2 = PhysicalSku using dbTypeId annotation values
   @Sync(resetState=true, initDefault=true)
   Sku sku;
   @Sync(resetState=true, initDefault=true)
   PhysicalSku psku;

   @Sync(resetState=true, initDefault=true)
   String skuCode;

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
   @Sync(resetState=true, initDefault=true)
   boolean productSaved;

   @Sync(initDefault=true)
   List<Sku> matchingSkus;

   @Sync(initDefault=true)
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

   @Sync(initDefault=true)
   List<Sku> missingSkuOptions;
   @Sync(initDefault=true)
   List<Sku> validSkuOptions;
   @Sync(initDefault=true)
   List<Sku> invalidSkuOptions;

   String searchStatusMessage = "";

   void resetForm() {
      super.resetForm();
      productList = null;
      element = null;
      productSaved = false;
      clearFormErrors();
      parentCategoryPathName = "";
      searchStatusMessage = "";
      sku = null;
      skuCode = null;
      productPathName = null;
      showSkuView = false;
      matchingSkus = null;
      matchingOptionSchemes = null;
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

   @Sync(resetState=true, initDefault=true)
   boolean autoUpdatePath = false;

   void updateProductName(String val) {
      if (product == null)
         return;
      product.name = val;
      if (autoUpdatePath && (val != null && val.length() > 0))
         product.pathName = ManagedResource.convertToPathName(val);
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
      productPathName = pathName;
   }

   String getElementType() {
      return "product";
   }
}
