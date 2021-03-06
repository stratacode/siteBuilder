@DBTypeSettings
@FindBy(name="userPending", with="user,pending,store", orderBy="-lastModified")
@Sync(onDemand=true)
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

   String currencyName;

   Currency getCurrency() {
      return Currency.getForName(currencyName);
   }

   String getCurrencySymbol() {
      if (totalPrice == null)
         return "";
      return currency.symbol;
   }

   boolean checkoutStarted;
   String orderNumber;
   boolean pending := orderNumber == null;
   boolean shipped := shippedOn != null;
   boolean cancelled := cancelledOn != null;
   boolean pendingShip := !pending && !shipped && !cancelled;

   boolean shipmentStarted = false;

   Date submittedOn;
   Date shippedOn;
   Date cancelledOn;
   Date lastModified;

   PaymentInfo paymentInfo;

   lastModified =: Bind.sendChangedEvent(this, "displayStatus");

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

   void refreshLineItems() {
      numLineItems = lineItems == null ? 0 : lineItems.size();
      refreshTotalPrice();
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
      else if (shippedOn == null) {
         if (shipmentStarted)
            return "partially shipped";
         if (cancelledOn != null) {
            return "cancelled " + TextUtil.formatUserDate(cancelledOn, true);
         }
         return "submitted " + TextUtil.formatUserDate(submittedOn, true);
      }
      else
         return "shipped " + TextUtil.formatUserDate(shippedOn, true);
   }

   public String getOrderSummary() {
      return "Order number: " + orderNumber + " submitted on " + submittedOn + " for total: " + currency.symbol + totalPrice;
   }
}
