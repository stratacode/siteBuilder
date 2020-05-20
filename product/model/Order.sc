@DBTypeSettings
class Order {
   List<LineItem> lineItems;
   BigDecimal totalPrice;
   int numItems;
   List<Coupon> cartCoupons;

   UserProfile user;

   Address shippingAddress;
   Address billingAddress;

   Currency currency;

   OrderStatus status;

   Date lastModified;
}
