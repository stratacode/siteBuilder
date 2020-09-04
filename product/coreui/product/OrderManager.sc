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

   String orderStatusMessage;
   String orderErrorMessage;

   void resetForm() {
      orderStatusMessage = null;
      orderErrorMessage = null;

      orderList = null;
      order = null;
      searchText = null;
   }

   void clearSearch() {
      resetForm();
   }
}
