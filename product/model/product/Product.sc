/**
   This is the basic product object, a CatalogElement.

   Use sub-types or interfaces to add product features:  BrandedProduct, DigitalProduct, etc.
   The framework will let the management UI gather the concrete types in this hierarchy for
   creating specific types of products and letting you have more than one type in the same
   catalog.

   Use ProductType instances which associate a given class with a renderer and add rules for managing that type of product.  Each Product instance has a "ProductType productType" property.

   The ProductType should have a method - validateProduct(Product) which returns a list of PropertyErrors (name of property, error code, error args, error message) for the product.

   Use layers for cross-product policy options.  We could have for example "globalMultiSku" which
   adds the multiSku property to Product or brandedMultiSku which adds it at the BrandedProduct
   level.

   Products will have renderers.  there can be a ProductRenderer base class which we define
   that's UI independent.  When you add a particular UI in a layer, it customizes the ProductRenderer
   to be specific to that framework - e.g. it could be an SCHTML type.

   Each class can have a defaultRenderer - used for all instances of that class which do not specify a template of their own.

   Each ProductType can specify a ProductRenderer type used for that ProductType.

   Question: should we push up the ProductClass, ProductType pattern so it's easy to use this for any manageable entity (e.g. sku, category)   ManageableClass, ManageableType, ManageableRenderer.  If so, ManagealbeType can have a list of labels - e.g. catalogElement, product to which this type pertains.  That lets the UI group each manageable thing according to more than one label.

   Question: should a ManageableClass have more than one ProductType?  Or if we need more than one dimension to categorize a manageable entity do we just add another property?  It seems like one productType is enough.  If it really represents a way to create and managea a "renderable thing", if we have a new way to render that thing in a different context, that's really a different property of that thing.

 */

// Make synchronized and persistent as a sub-type of CatalogElement with sub-type id 1
@Sync(onDemand=true)
@DBTypeSettings(typeId=1)
@sc.js.JSSettings(dependentTypes="sc.product.PhysicalSku,sc.content.ManagedImage")
@sc.obj.SyncTypeFilter(typeNames={"sc.product.PhysicalSku", "sc.content.ManagedImage"})
class Product extends CatalogElement {
   override @DBPropertySettings(reverseProperty="childProducts") parentCategory;

   // Theses are all additional text segments you can display.  As they are populated
   // the template will adjust itself to display them.
   // TODO: could be in a separate layer but for such basic things for now, we'll put them here and let you toggle on/off visibility in the editor
   String productFeatures;
   String sellTagline;
   String moreInfo;
   String specs;
   String vendor;

   int defaultQuantity = 1;

   /*
   @Sync(onDemand=true)
   @DBPropertySettings(columnType="jsonb")
   List<RelatedProduct> relatedProducts;
   */

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
