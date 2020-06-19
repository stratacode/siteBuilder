Order {
   String validateForCheckout() {
      // Add rules like in-stock checks to be run before we allow them to begin the checkout process
      return null;
   }

   String validateForSubmit() {
      // Add rules for validating payment, address, etc. Per-property validation will have already been performed.
      return null;
   }

   static Order createDraft(Storefront store, UserProfile user) {
      Order order = new Order();
      order.user = user;
      order.store = store;
      Currency currency = Currency.currencyForLanguageTag.get(user.localeTag);
      if (currency == null || !store.supportsCurrency(currency))
         currency = store.defaultCurrency;
      order.currencyName = currency.currencyName;
      if (user.homeAddress != null) {
         order.shippingAddress = user.homeAddress;
         order.billingAddress = user.homeAddress;
      }
      else {
         order.shippingAddress = new Address();
         order.billingAddress = order.shippingAddress;
      }
      if (user.paymentInfo == null) {
         order.paymentInfo = new PaymentInfo();
         order.paymentInfo.cardHolder = user.name;
      }
      else
         order.paymentInfo = user.paymentInfo;
      order.dbInsert(false);
      return order;
   }

}