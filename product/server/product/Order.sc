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

   void appendOrder(Order other) {
      if (other.numLineItems > 0) {
         List<LineItem> newLineItems = new ArrayList<LineItem>();
         for (int i = 0; i < other.lineItems.size(); i++) {
            LineItem newLineItem = other.lineItems.get(i).copyLineItem();
            newLineItem.order = this;
            newLineItems.add(newLineItem);
         }
         if (lineItems == null)
            lineItems = newLineItems;
         //else - because setting the order above automatically adds it to the line items array
         //   lineItems.addAll(0, newLineItems);
         numLineItems = lineItems.size();
      }
   }

   String allocateInventory() {
      if (lineItems != null) {
         Map<String,Integer> skuCodeQuantities = new java.util.HashMap<String,Integer>();
         ArrayList<Sku> invSkus = new ArrayList<Sku>();
         for (LineItem lineItem:lineItems) {
            if (lineItem.sku instanceof PhysicalSku) {
               PhysicalSku sku = (PhysicalSku) lineItem.sku;
               ProductInventory inventory = sku.inventory;
               if (inventory != null) {
                  // Account for two line items referring to the same sku
                  Integer alloced = skuCodeQuantities.get(sku.skuCode);
                  if (alloced == null)
                     alloced = 0;

                  int needed = lineItem.quantity + alloced;
                  if (inventory.quantity == 0) {
                     return "Sku: " + sku.skuCode + " is out of stock";
                  }
                  else if (inventory.quantity < needed) {
                     return "Sku: " + sku.skuCode + " has only: " + inventory.quantity + " available - order requires: " + needed;
                  }
                  if (skuCodeQuantities.put(sku.skuCode, lineItem.quantity + alloced) == null)
                     invSkus.add(sku);
               }
            }
         }

         if (invSkus.size() > 0) {
            DBTransaction curr = DBTransaction.getCurrent();
            if (curr != null)
               curr.commit();

            DBTransaction itx = DBTransaction.getOrCreate();
            for (Sku invSku:invSkus) {
               Integer quant = skuCodeQuantities.get(invSku.skuCode);
               ProductInventory inv = invSku.inventory;
               if (inv.quantity >= quant) {
                  inv.quantity = inv.quantity - quant;
                  Bind.sendChangedEvent(invSku, "inventoryDisplayStr");
               }
               else {
                  itx.rollback();
                  String message = "Inventory for product: " + invSku.skuCode + ": " + inv.quantity + " changed! It's now less than the required inventory for order: " + quant;
                  DBUtil.error("Rolling back inventory transaction: " + message);
                  return message;
               }
            }
            try {
               itx.commit();
               return null;
            }
            catch (Exception exc) {
               DBUtil.error("Exception committing inventory: " + exc);
            }
            return "Error committing inventory changes for order";
         }
      }
      return null;
   }

   int getReservedInventory(PhysicalSku sku) {
      if (lineItems == null)
         return 0;
      int ct = 0;
      for (LineItem lineItem:lineItems) {
         if (lineItem.sku == sku)
            ct += lineItem.quantity;
      }
      return ct;
   }

}
