@DBTypeSettings
class LineItem {
   @DBPropertySettings(reverseProperty="lineItems")
   Order order;

   @DBPropertySettings(indexed=true)
   Product product;

   @Sync(initDefault=true)
   Sku sku;
   int quantity;
   Date lastModified;

   @DBPropertySettings(columnType="jsonb")
   List<Sku> skuParts;

   @DBPropertySettings(columnType="jsonb")
   List<Coupon> lineItemCoupons;

   BigDecimal tax;
   BigDecimal totalPrice;

   static LineItem create(Order order, Product product, Sku sku, List<Sku> skuParts, int quantity) {
      if (quantity == 0)
         return null;
      if (quantity < 0)
         throw new IllegalArgumentException("Invalid quantity for line item");
      LineItem res = new LineItem();
      res.order = order;
      res.product = product;
      res.sku = sku;
      res.quantity = quantity;
      res.dbInsert(false);
      res.skuParts = skuParts;
      res.refreshLineItemPrice();
      return res;
   }

   void refreshLineItemPrice() {
      BigDecimal itemPrice = sku.priceToUse;
      if (skuParts != null) {
         for (Sku skuPart:skuParts) {
            itemPrice = skuPart.priceToUse.add(itemPrice);
         }
      }
      totalPrice = itemPrice.multiply(new BigDecimal(quantity));
   }
}
