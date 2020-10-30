/**
 * This is the basic product object, stored mapped to a table in the database
 * and
 */
// Sync to the client
@Sync(onDemand=true)
// Make synchronized and persistent as a sub-type of CatalogElement with sub-type id 1
@DBTypeSettings(typeId=1)
// Pulls in some sub-classes of the classes referenced so included in the sync and JS code
@sc.js.JSSettings(dependentTypes="sc.product.PhysicalSku,sc.content.ManagedImage")
@sc.obj.SyncTypeFilter(typeNames={"sc.product.PhysicalSku", "sc.content.ManagedImage"})
class Product extends CatalogElement {
   override @DBPropertySettings(reverseProperty="childProducts") parentCategory;

   String productFeatures;
   String sellTagline;
   String moreInfo;
   String specs;
   String vendor;

   int defaultQuantity = 1;

   Brand brand;

   /** The main SKU for the product */
   @Sync(resetState=true,initDefault=true)
   Sku sku;

   boolean saleItem := sku.discountPrice != null;

   // Could be in a promotions layer but also pretty basic so might be best left in the core model but enabled in various layer configurations.
   @DBPropertySettings(columnType="jsonb")
   List<Promotion> promotions;

   @DBPropertySettings(columnType="jsonb")
   List<Product> accessories;

   @Sync(onDemand=true)
   @DBPropertySettings(columnType="jsonb")
   List<Product> productCrossSells;

   @Sync(onDemand=true)
   @DBPropertySettings(columnType="jsonb")
   List<Product> productUpSells;

   String pageUrl := "/" + store.sitePathTypeName + "/" + store.sitePathName + "/product/" + pathName;

   @sc.obj.EditorCreate(constructorParamNames="parentCategory, name, pathName")
   static Product createProduct(Category parentCategory, String name, String pathName) {
      Product newProduct = new Product();
      newProduct.name = name;
      newProduct.pathName = pathName;
      newProduct.parentCategory = parentCategory;
      newProduct.dbInsert(false);
      return newProduct;
   }

   override @FindBy(paged=true,orderBy="-lastModified",with="store") pathName;

   ManagedMedia getMediaForSku(Sku mediaSku) {
      if (mediaSku == null || mediaSku.options == null)
         return mainMedia;

      ManagedMedia defaultMedia = null;

      List<ManagedMedia> mediaList = altMedia;
      for (int i = 0; i < mediaList.size(); i++) {
         ManagedMedia media = mediaList.get(i);
         String filterPattern = media.filterPattern;
         if (filterPattern == null) {
            if (defaultMedia == null)
               defaultMedia = media;
         }
         else {
            String next = filterPattern;
            boolean match = true;
            List<OptionValue> skuOpts = mediaSku.options;
            do {
               int rix = next.indexOf(",");
               String optStr = rix == -1 ? next : next.substring(0, rix);
               int eix = optStr.indexOf("=");
               if (eix == -1) {
                  match = false;
                  System.err.println("*** Invalid filterPattern for media: " + media.uniqueFileName);
                  break;
               }
               String optName = optStr.substring(0, eix);
               if (optStr.length() <= eix + 1) {
                  match = false;
                  break;
               }
               String optVal = optStr.substring(eix+1);
               boolean found = false;
               for (int j = 0; j < skuOpts.size(); j++) {
                  if (sku.optionScheme.options.get(j).optionName.equals(optName)) {
                     if (!skuOpts.get(j).optionValue.equals(optVal)) {
                        match = false;
                        break;
                     }
                     found = true;
                     break;
                  }
               }
               if (!found) {
                  System.err.println("*** Option: " + optStr + " in filterPattern for media: " + media.uniqueFileName + " not found in optionScheme: " + sku.optionScheme);
                  match = false;
                  break;
               }
               if (rix == -1 || next.length() <= rix+1)
                  break;
               next = next.substring(rix+1);
            } while(true);
            if (match) {
               return media;
            }
         }
         if (defaultMedia != null)
            return defaultMedia;
      }
      return mainMedia;
   }
}
