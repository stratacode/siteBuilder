@DBTypeSettings
class LineItem {
   Product product;
   Sku sku;
   int quantity;
   Date lastModified;

   String shortDesc;

   List<OptionValue> options;

   List<Coupon> lineItemCoupons;

   BigDecimal totalPrice;
}
