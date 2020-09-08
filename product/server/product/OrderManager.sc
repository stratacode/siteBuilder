import java.util.Arrays;

OrderManager {
   static final List<String> orderSearchOrderBy = Arrays.asList("-lastModified");
   static final List<String> searchAllOrderProps = Arrays.asList("store");

   void addSearchProps(OrderSearchType searchType, List<String> propNames, List<Object> propValues) {
      propNames.add("store");
      propValues.add(store);
      switch (searchType) {
         case All:
            break;
         case PendingCheckout:
            propNames.add("orderNumber");
            propValues.add(null);
            break;
         case PendingShip:
            propNames.add("pendingShip");
            propValues.add(true);
            break;
         case Shipped:
            propNames.add("shipped");
            propValues.add(true);
            break;
         case Cancelled:
            propNames.add("cancelled");
            propValues.add(true);
            break;
         default:
            throw new UnsupportedOperationException();
      }
   }

   void doSearch() {
      String txt = searchText == null ? "" : searchText;
      ArrayList<String> propNames = new ArrayList<String>();
      ArrayList<Object> propValues = new ArrayList<Object>();
      addSearchProps(orderSearchType, propNames, propValues);

      orderList = (List<Order>) Order.getDBTypeDescriptor().searchQuery(txt, propNames, propValues, null, orderSearchOrderBy, -1, -1);
   }

   void doSelectOrder(Order toSel) {
      orderStatusMessage = null;
      orderErrorMessage = null;
      // We might have just removed this order so don't make it current again
      if (((DBObject)toSel.getDBObject()).isActive()) {
         if (toSel == order) {
            order = null;
         }
         else {
            order = toSel;
         }
      }
   }

   void markOrderShipped(Order order) {
      order.shippedOn = new Date();
      order.shipmentStarted = true;
      List<String> unavailableSkus = new ArrayList<String>();
      for (LineItem lineItem: order.lineItems) {
         Sku sku = lineItem.sku;
         if (sku instanceof PhysicalSku) {
            PhysicalSku psku = (PhysicalSku) sku;
            if (psku.inventory != null) {
               int quant = psku.inventory.quantity;
               if (quant <= 0)
                  unavailableSkus.add(psku.skuCode);
            }
         }
         lineItem.shippedOn = order.shippedOn;
      }
      if (unavailableSkus.size() > 0) {
         orderErrorMessage = "Warning: No inventory to ship skus: " + unavailableSkus;
         orderStatusMessage = null;
      }
      else {
         orderStatusMessage = "Order marked as shipped";
         orderErrorMessage = null;
      }
      Bind.sendChangedEvent(order, "displayStatus");
   }

   void cancelOrder(Order order) {
      if (!order.pendingShip) {
         orderErrorMessage = "Unable to cancel order not yet submitted";
         return;
      }
      order.cancelledOn = new Date();
      List<LineItem> shippedItems = new ArrayList<LineItem>();
      int numCancelled = 0;
      for (LineItem lineItem: order.lineItems) {
         if (lineItem.shippedOn != null) {
            shippedItems.add(lineItem);
            continue;
         }
         numCancelled++;
         Sku sku = lineItem.sku;
         if (sku instanceof PhysicalSku) {
            PhysicalSku psku = (PhysicalSku) sku;
            if (psku.inventory != null) {
               psku.inventory.quantity += lineItem.quantity;
            }
         }
      }
      if (shippedItems.size() > 0) {
         orderStatusMessage = "Cancelled remaining " + numCancelled + " line items - " + shippedItems.size() + " already shipped";
      }
      else
         orderStatusMessage = "Order cancelled";
      Bind.sendChangedEvent(order, "displayStatus");
   }

   void markItemShipped(Order order, LineItem lineItem) {
      lineItem.shippedOn = new Date();
      order.shipmentStarted = true;
      boolean notShipped = false;
      for (LineItem nextLineItem:order.lineItems) {
         if (nextLineItem.shippedOn == null) {
            notShipped = true;
            break;
         }
      }
      if (!notShipped)
         markOrderShipped(order);
      else
         Bind.sendChangedEvent(order, "displayStatus");
   }

   void cancelLineItem(Order order, LineItem lineItem) {
      if (!order.lineItems.remove(lineItem))
         return;
      if (order.lineItems.size() == 0)
         cancelOrder(order);
      else
         order.refreshLineItems();
      Bind.sendChangedEvent(order, "displayStatus");
   }

   void updateSearchType(OrderSearchType searchType) {
      orderSearchType = searchType;
      doSearch();
   }

}
