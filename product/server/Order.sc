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
      boolean newPaymentInfo = false;
      if (user.paymentInfo == null) {
         order.paymentInfo = new PaymentInfo();
         order.paymentInfo.cardHolder = user.name;
         newPaymentInfo = true;
      }
      else
         order.paymentInfo = user.paymentInfo;
      if (user.homeAddress != null) {
         order.shippingAddress = user.homeAddress;
         if (newPaymentInfo || order.paymentInfo.billingAddress == null)
            order.paymentInfo.billingAddress = user.homeAddress;
      }
      else {
         order.shippingAddress = new Address();
         if (newPaymentInfo)
            order.paymentInfo.billingAddress = order.shippingAddress;
      }
      if (order.paymentInfo.billingAddress == null)
         order.paymentInfo.billingAddress = order.shippingAddress;
      order.dbInsert(false);
      return order;
   }

}