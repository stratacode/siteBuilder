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

   Date shippedOn;

   @DBPropertySettings(columnType="jsonb")
   List<Coupon> lineItemCoupons;

   BigDecimal tax;
   BigDecimal totalPrice;

   static LineItem create(Order order, Product product, Sku sku, int quantity) {
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
      res.refreshLineItemPrice();
      return res;
   }

   void refreshLineItemPrice() {
      BigDecimal itemPrice = sku.priceToUse;
      totalPrice = itemPrice.multiply(new BigDecimal(quantity));
   }

   LineItem copyLineItem() {
      LineItem newLineItem = new LineItem();
      newLineItem.product = product;
      newLineItem.sku = sku;
      newLineItem.quantity = quantity;
      newLineItem.lineItemCoupons = lineItemCoupons;
      newLineItem.tax = tax;
      newLineItem.totalPrice = totalPrice;
      return newLineItem;
   }
}
