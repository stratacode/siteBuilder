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

@DBTypeSettings(typeId=1)
class Product extends CatalogElement {
   override @DBPropertySettings(reverseProperty="products") parentCategory;

   // Theses are all additional text segments you can display.  As they are populated
   // the template will adjust itself to display them.
   // TODO: could be in a separate layer but for such basic things for now, we'll put them here and let you toggle on/off visibility in the editor
   String productFeatures;
   String sellTagline;
   String moreInfo;
   String specs;
   String vendor;

   List<RelatedProduct> relatedProducts;

   Brand brand;

   /** The main SKU for the product */
   Sku sku;

   /* List of parts for this product */
   List<Sku> skuParts;

   /* Used for when there are options to hold the sku's for each available option combination */
   List<Sku> skuOptions;

   /** Reference to the list of options for this product (if any).  */
   ProductOptions options;

   // Could be in a promotions layer but also pretty basic so might be best left in the core model but enabled in various layer configurations.
   List<Promotion> promotions;

   List<Product> accessories;

   List<Product> productCrossSells;

   List<Product> productUpSells;

   override @FindBy(paged=true,orderBy="-lastModified") pathName;

   Sku getSkuForOptionsWith(List<OptionValue> optValues, int overrideIx, OptionValue overrideVal) {
      if (skuOptions == null) {
         return null;
      }
      int numOptions = optValues.size();
      for (Sku sku:skuOptions) {
         for (OptionValue skuOpt:sku.options) {
            boolean matched = true;
            for (int i = 0; i < numOptions; i++) {
               OptionValue toCompare = i == overrideIx ? overrideVal : sku.options.get(i);
               if (!toCompare.name.equals(skuOpt.name)) {
                  matched = false;
                  break;
               }
            }
            if (matched) {
               return sku;
            }
         }
      }
      return null;
   }
}
