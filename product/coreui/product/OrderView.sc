@Component
class OrderView {
   @Sync
   int numLineItems := order == null ? 0 : order.numLineItems;

   @Sync
   Order order = null;

   @Sync
   String orderError;

}
