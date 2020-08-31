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
         case Pending:
            propNames.add("orderNumber");
            propValues.add(null);
            break;
         case Submitted:
            propNames.add("submitted");
            propValues.add(true);
            break;
         case Delivered:
            propNames.add("delivered");
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
      resetForm();
      // We might have just removed this order so don't make it current again
      if (((DBObject)toSel.getDBObject()).isActive()) {
         if (toSel == order) {
         }
         else {
            order = toSel;
         }
      }
   }

   void markOrderDelivered(Order order) {
      order.deliveredOn = new Date();
      List<String> unavailableSkus = new ArrayList<String>();
      for (LineItem lineItem: order.lineItems) {
         Sku sku = lineItem.sku;
         if (sku instanceof PhysicalSku) {
            PhysicalSku psku = (PhysicalSku) sku;
            if (psku.inventory != null) {
               int quant = psku.inventory.quantity;
               if (quant > 0) {
                  psku.inventory.quantity = quant - 1;
               }
               else
                  unavailableSkus.add(psku.skuCode);
            }
         }
      }
      if (unavailableSkus.size() > 0) {
         orderErrorMessage = "No inventory for skus: " + unavailableSkus;
         orderStatusMessage = null;
      }
      else {
         orderStatusMessage = "Order marked as delivered";
         orderErrorMessage = null;
      }
   }

   void updateSearchType(OrderSearchType searchType) {
      orderSearchType = searchType;
      doSearch();
   }

}
