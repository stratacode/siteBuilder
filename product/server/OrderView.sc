OrderView {
   void init() {
      refresh();
   }

   void pageVisited() {
      newOrderSubmitted = false;
      refresh();
   }

   void refresh() {
      UserProfile user = currentUserView.user;
      orderError = null;
      if (user != null && !user.getDBObject().isTransient()) {
         List<Order> userOrders = Order.findByUserPending(user, true);
         // TODO: should we have a pendingOrderId in the UserProfile to identify this without a general query?
         if (userOrders != null && userOrders.size() > 0) {
            if (userOrders.size() > 1)
               System.err.println("*** Warning - more than one unsubmitted order for user: " + user);

            Order newOrder = userOrders.get(0);
            if (newOrder != order) {
               order = userOrders.get(0);
            }
         }

         if (order != null) {
            List<LineItem> lineItems = order.lineItems;
            if (lineItems != null) {
               for (LineItem lineItem:order.lineItems) {
                  if (lineItem.product != null) {
                     SyncContext syncCtx = SyncManager.getSyncContextForInst(lineItem.product);
                     if (syncCtx == null) {
                        SyncManager.addSyncInst(lineItem.product, false, false, false, "appSession", null);
                     }
                     SyncManager.startSync(lineItem.product, "options");
                  }
               }
            }

            Address billingAddress = order.paymentInfo.billingAddress;
            Address shippingAddress = order.shippingAddress;
            if (billingAddress == null) {
               billingAddress = shippingAddress;
               order.paymentInfo.billingAddress = billingAddress;
            }
            billToShipping = billingAddress == shippingAddress;

            boolean validShipping = false;
            if (shippingAddress != null && !shippingAddress.treatAsEmpty) {
               shippingAddress.validateAddress();
               if (shippingAddress.propErrors == null)
                  validShipping = true;
            }
            validAddress = validShipping;
            if (!validAddress)
               editAddress = true;
            else
               editAddress = false;

            order.emailAddress = user.emailAddress;

            if (user.paymentInfo != null)
               editPayment = false;
         }
         else {
            validAddress = false;
            validPayment = false;
            editAddress = true;
            editPayment = true;
         }
      }
   }

   void addLineItem(Product product, Sku sku, int quantity) {
      if (quantity <= 0) {
         orderError = "Not adding 0 quantity sku to order";
         return;
      }
      if (sku == null) {
         orderError = "Missing sku";
         return;
      }

      UserProfile user = currentUserView.user;
      if (order == null) {
         order = Order.createDraft(store, user);
      }
      if (order.lineItems == null)
         order.lineItems = new ArrayList<LineItem>();
      LineItem newLineItem = LineItem.create(order, product, sku, quantity);
      refreshLineItems();
   }

   void refreshLineItems() {
      order.numLineItems = order.lineItems.size();
      order.refreshTotalPrice();
   }

   void changeQuantity(LineItem lineItem, String quantityStr) {
      int quantity;
      try {
         quantity = Integer.parseInt(quantityStr);
      }
      catch (NumberFormatException exc) {
         orderError = "Invalid number: " + quantityStr;
         return;
      }
      if (order == null || !order.lineItems.contains(lineItem))
         orderError = "Line item not found for changeQuantity";
      else {
         if (quantity < 0)
            orderError = "Invalid quantity: " + quantity;
         else if (quantity == 0)
            deleteLineItem(lineItem);
         else if (quantity > Storefront.MaxQuantity)
            orderError = "Quantity is greater than the max: " + Storefront.MaxQuantity;
         else {
            lineItem.quantity = quantity;
            lineItem.refreshLineItemPrice();
            order.refreshTotalPrice();
         }
      }
   }

   void deleteLineItem(LineItem lineItem) {
      if (order == null || !order.lineItems.contains(lineItem))
         orderError = "Line item not found for delete";
      order.lineItems.remove(lineItem);
      refreshLineItems();
   }

   void performAction() {
      clearErrors();
      if (order != null) {
         if (!order.checkoutStarted)
            startCheckout();
         else if (order.orderNumber == null)
            submitOrder();
         else
            orderError = "Order has already been submitted: " + order.orderNumber;
      }
   }

   void startCheckout() {
      Map<String,String> propErrors = order.dbValidate();
      if (propErrors == null) {
         orderError = order.validateForCheckout();
         if (orderError == null) {
            order.checkoutStarted = true;
            try {
               order.dbUpdate();
            }
            catch (RuntimeException exc) {
               orderError = "Software error starting checkout - please try again or contact us";
               order.checkoutStarted = false;
               DBUtil.error("Error updating order for startCheckout: " + exc);
            }
         }
      }
   }

   void validateProperties() {
      propErrors = DynUtil.validateProperties(order, null);
   }

   void submitOrder() {
      validateProperties();
      if (order.shippingAddress.country == null)
         order.shippingAddress.country = store.defaultCountry;
      order.shippingAddress.validateAddress();
      if (order.shippingAddress != order.paymentInfo.billingAddress) {
         if (order.paymentInfo.billingAddress.country == null)
            order.paymentInfo.billingAddress.country = store.defaultCountry;
         order.paymentInfo.billingAddress.validateAddress();
      }
      order.paymentInfo.validatePaymentInfo();
      orderError = order.validateForSubmit();
      if (propErrors == null && order.paymentInfo.billingAddress.propErrors == null && order.shippingAddress.propErrors == null &&
          orderError == null && order.paymentInfo.propErrors == null) {

         newOrderSubmitted = true;
         order.orderNumber = order.store.makeOrderNumber(order);
         order.submittedOn = new Date();
         try {
            order.dbUpdate();

            completedOrder = order;

            UserProfile user = currentUserView.user;
            if (user == null) {
               DBUtil.error("After submitting order - no user found");
               order = null;
               return;
            }

            if (user.registered) {
               confirmDefaultAddress = false;
               confirmDefaultPayment = false;
               if (order.shippingAddress != null) {
                  List<Address> addrs = user.addresses;
                  boolean setAddrs = false;
                  if (addrs == null) {
                     addrs = new BArrayList<Address>();
                     setAddrs = true;
                  }
                  if (!addrs.contains(order.shippingAddress))
                     addrs.add(order.shippingAddress);

                  if (setAddrs)
                     user.addresses = addrs;

                  if (user.homeAddress == null)
                     user.homeAddress = order.shippingAddress;
                  else if (!user.homeAddress.equals(order.shippingAddress))
                     confirmDefaultAddress = true;
               }
               if (user.savePaymentInfo && order.paymentInfo != null) {
                  List<PaymentInfo> pis = user.paymentInfos;
                  boolean setPis = false;
                  if (pis == null) {
                     pis = new ArrayList<PaymentInfo>();
                     setPis = true;
                  }
                  if (!pis.contains(order.paymentInfo))
                     pis.add(order.paymentInfo);
                  if (user.paymentInfo == null)
                     user.paymentInfo = order.paymentInfo;
                  // Using a different payment info - should it be the new default?
                  else if (!user.paymentInfo.equals(order.paymentInfo))
                     confirmDefaultPayment = true;

                  if (setPis)
                     user.paymentInfos = pis;
               }
            }

            order = Order.createDraft(store, user);
         }
         catch (RuntimeException exc) {
            orderError = "Software error submitting order - please try again or contact us";
            order.orderNumber = null;
            DBUtil.error("Error submitting order: " + exc);
         }
      }
   }

   void billToShippingChanged() {
      if (order == null)
         return;
      if (billToShipping) {
         if (order.shippingAddress != order.paymentInfo.billingAddress)
            order.paymentInfo.billingAddress = order.shippingAddress;
      }
      else {
         if (order.paymentInfo.billingAddress == order.shippingAddress) {
            order.paymentInfo.billingAddress = new Address();
            order.paymentInfo.billingAddress.dbInsert(true);
         }
      }
   }

   void updateEmailAddress(String email) {
      if (order == null)
         return;
      order.emailAddress = email;
      validateProperties();
   }

   void changeShippingAddress(Address addr) {
      order.shippingAddress = addr;
   }

   void startNewAddress() {
      Address newAddr = new Address();
      if (order.shippingAddress != null)
         newAddr.name = order.shippingAddress.name;
      order.shippingAddress = newAddr;
      editAddress = true;
   }

   void cancelNewAddress() {
      if (userView.user.homeAddress != null) {
         order.shippingAddress = userView.user.homeAddress;
         editAddress = false;
      }
   }

   void changePaymentInfo(PaymentInfo pi) {
      order.paymentInfo = pi;
   }

   void startNewPaymentInfo() {
      PaymentInfo pi = new PaymentInfo();
      if (userView.user.homeAddress != null)
         pi.billingAddress = userView.user.homeAddress;
      else
         pi.billingAddress = new Address();
      order.paymentInfo = pi;
      editPayment = true;
   }

   void changeDefaultAddress(Address address) {
      userView.changeHomeAddress(address);
      if (order.orderNumber == null)
         order.shippingAddress = address;
      confirmDefaultAddress = false;
   }

   void changeDefaultPaymentInfo(PaymentInfo paymentInfo) {
      userView.changePaymentInfo(paymentInfo);
      if (order.orderNumber == null)
         order.paymentInfo = paymentInfo;
      confirmDefaultPayment = false;
   }
}
