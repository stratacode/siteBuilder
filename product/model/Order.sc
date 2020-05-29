@DBTypeSettings
@FindBy(name="userAndStatus", with="user,status", findOne=true)
class Order {
   List<LineItem> lineItems;
   BigDecimal totalPrice;
   int numLineItems;
   List<Coupon> cartCoupons;

   UserProfile user;

   Address shippingAddress;
   Address billingAddress;

   String currencyName;

   Currency getCurrency() {
      return Currency.getForName(currencyName);
   }

   OrderStatus status;

   Date lastModified;

   static Order createDraft(Storefront store, UserProfile user) {
      Order order = new Order();
      order.user = user;
      order.status = OrderStatus.Draft;
      Currency currency = Currency.currencyForLanguageTag.get(user.localeTag);
      if (currency == null || !store.supportsCurrency(currency))
         currency = store.defaultCurrency;
      order.currencyName = currency.currencyName;
      if (user.homeAddress != null) {
         order.shippingAddress = user.homeAddress;
         order.billingAddress = user.homeAddress;
      }
      order.dbInsert(false);
      return order;
   }

   void refreshTotalPrice() {
      BigDecimal res = new BigDecimal("0.00");
      List<LineItem> items = lineItems;
      if (items != null) {
         for (LineItem lineItem:items) {
            res = res.add(lineItem.totalPrice);
         }
      }
      totalPrice = res;
   }
}
