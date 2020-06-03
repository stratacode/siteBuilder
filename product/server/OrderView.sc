OrderView {
   void init() {
      refresh();
   }

   void refresh() {
      UserProfile user = currentUserView.user;
      orderError = null;
      if (user != null && !user.getDBObject().isTransient()) {
         order = Order.findByUserAndStatus(user, OrderStatus.Draft);

         if (order != null) {
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
      }
   }

   void addLineItem(Product product, Sku sku, List<Sku> skuParts, int quantity) {
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
         order = Order.createDraft(StoreView.store, user);
      }
      if (order.lineItems == null)
         order.lineItems = new ArrayList<LineItem>();
      LineItem newLineItem = LineItem.create(order, product, sku, skuParts, quantity);
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
}
