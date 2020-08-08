OrderView {
   void init() {
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
                        SyncManager.addSyncInst(lineItem.product, false, false, "appSession", null);
                     }
                     SyncManager.startSync(lineItem.product, "options");
                  }
               }
            }

            billToShipping = order.billingAddress == order.shippingAddress;
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
      if (order.shippingAddress != order.billingAddress) {
         if (order.billingAddress.country == null)
            order.billingAddress.country = store.defaultCountry;
         order.billingAddress.validateAddress();
      }
      order.paymentInfo.validatePaymentInfo();
      orderError = order.validateForSubmit();
      if (propErrors == null && order.billingAddress.propErrors == null && order.shippingAddress.propErrors == null &&
          orderError == null && order.paymentInfo.propErrors == null) {
         order.orderNumber = order.store.makeOrderNumber(order);
         order.submittedOn = new Date();
         try {
            order.dbUpdate();

            completedOrder = order;
            order = Order.createDraft(store, currentUserView.user);
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
         if (order.shippingAddress != order.billingAddress)
            order.billingAddress = order.shippingAddress;
      }
      else {
         if (order.billingAddress == order.shippingAddress) {
            order.billingAddress = new Address();
            order.billingAddress.dbInsert(true);
         }
      }
   }

   void updateEmailAddress(String email) {
      if (order == null)
         return;
      order.emailAddress = email;
      validateProperties();
   }
}
