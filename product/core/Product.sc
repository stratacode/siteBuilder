/** 
 * The product will represent a family of skus like in most ecommerce systems.  Here there's
 * always a current sku which reflects the current set of product option properties which 
 * contribute to the sku's definition.  We use regular properties in the product for product
 * options so we can use Java type hierarchy etc.  As those option properties are changed
 * we regenerate the current sku property.  As the sku is changed, we'll similarly adjust
 * the option properties affected by that sku.  The sku-id is generated via a configurable
 * rule from the set of option properties.
 */
/** 
The basic product structure and hinge points for extensions:

Use sub-types for high-level types of products - BrandedProduct, DigitalProduct, etc.
The framework will let the management UI gather the concrete types in this hierarchy for
creating specific types of products and letting you have more than one type in the same 
catalog.

Use ProductType instances which associate a given class with a renderer and add rules for managing that type of product.  Each Product then has a ProductType productType property.

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

class Product extends CatalogElement {
   // Theses are all additional text segments you can display.  As they are populated
   // the template will adjust itself to display them.  
   // TODO remove from the base product?
   String productFeatures;
   String techSpecs;
   String moreInfo;
   String sellTagline;

   // Layer: relatedProducts
   List<RelatedProduct> relatedProducts;

   // When you set the SKU it should populate any associated options and vice versa
   BaseSKU sku;

   // Layer: multiSku
   List<BaseSKU> productSkus;

   // ?? Joda money package or BigDecimal for storing prices?  Wrap in a container
   // so prices are more configurable
   Price price;

   // Promotions layer 
   List<Promotion> promotions; 

   // Discount layer - named
   Price salePrice;

   // Inventory layer
   ProductInventory inventory;

   // Product - options?  we'll represent them as real properties so we get the benefits of
   // the type system, better more efficient storage, etc.  But there's a @ProductOption annotation
   // we use so we can gather up all options for each type and support a reflective way to treat
   // special product options in the code.  
}
