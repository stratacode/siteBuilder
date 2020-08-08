@DBTypeSettings
@FindBy(name="userPending", with="user,pending", orderBy="-lastModified")
class Order {
   List<LineItem> lineItems;
   BigDecimal tax;
   BigDecimal totalPrice;
   int numLineItems;

   @DBPropertySettings(columnType="jsonb")
   List<Coupon> cartCoupons;

   UserProfile user;
   Storefront store;

   String emailAddress;
   Address shippingAddress;
   Address billingAddress;

   String currencyName;

   Currency getCurrency() {
      return Currency.getForName(currencyName);
   }

   boolean checkoutStarted;
   String orderNumber;
   boolean pending := orderNumber == null;
   boolean delivered := deliveredOn != null;
   boolean submitted := submittedOn != null && deliveredOn == null;

   Date submittedOn;
   Date deliveredOn;
   Date lastModified;

   PaymentInfo paymentInfo;

   void refreshTotalPrice() {
      BigDecimal res = new BigDecimal("0.00");
      List<LineItem> items = lineItems;
      if (items != null) {
         for (LineItem lineItem:items) {
            if (lineItem.totalPrice == null) {
               System.out.println("*** Warning - no saved totalPrice for lineItem - refreshing");
               lineItem.refreshLineItemPrice();
            }
            if (lineItem.totalPrice != null)
               res = res.add(lineItem.totalPrice);
            else
               System.err.println("*** Invalid line item - no total price");
         }
      }
      totalPrice = res;
   }

   String validateEmailAddress(String emailAddress) {
      return TextUtil.validateEmailAddress(emailAddress);
   }

   @Bindable(manual=true)
   public String getDisplayStatus() {
      if (!checkoutStarted)
         return "pending checkout";
      else if (submittedOn == null)
         return "pending submit";
      else if (deliveredOn == null)
         return "pending delivery";
      else
         return "order complete";
   }
}
