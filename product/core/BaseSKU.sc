class BaseSKU {
   // Backpointer to the product which owns this sku
   @Constant 
   Product product;
   String skuCode;
}
