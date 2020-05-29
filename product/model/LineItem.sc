@DBTypeSettings
class LineItem {
   @DBPropertySettings(reverseProperty="lineItems")
   Order order;

   Product product;
   Sku sku;
   int quantity;
   Date lastModified;

   List<Sku> skuParts;

   List<Coupon> lineItemCoupons;

   BigDecimal totalPrice;

   static LineItem create(Order order, Product product, Sku sku, List<Sku> skuParts, int quantity) {
      LineItem res = new LineItem();
      res.order = order;
      res.product = product;
      res.sku = sku;
      res.quantity = quantity;
      res.dbInsert(false);
      res.skuParts = skuParts;
      res.totalPrice = sku.priceToUse;
      if (skuParts != null) {
         for (Sku skuPart:skuParts) {
            res.totalPrice = skuPart.priceToUse.add(res.totalPrice);
         }
      }
      return res;
   }
}
