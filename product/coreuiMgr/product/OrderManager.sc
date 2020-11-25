@Component
@Sync
class OrderManager extends BaseManager {
   @Sync(initDefault=true)
   List<Order> orderList;

   OrderSearchType orderSearchType = OrderSearchType.All;

   @Sync(resetState=true, initDefault=true)
   String searchText;

   @Sync(resetState=true, initDefault=true)
   Order order;

   @Sync(initDefault=true)
   String searchStatusMessage;

   String orderStatusMessage;
   String orderErrorMessage;

   void resetForm() {
      orderStatusMessage = null;
      orderErrorMessage = null;

      searchStatusMessage = null;

      orderList = null;
      order = null;
      searchText = null;
   }

   void clearSearch() {
      resetForm();
   }
}
