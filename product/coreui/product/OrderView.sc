@Component @Sync
class OrderView {
   @Sync(initDefault=true)
   int numLineItems := order == null ? 0 : order.numLineItems;

   @Sync(initDefault=true)
   Order order = null;

   @Sync(initDefault=true)
   Order completedOrder = null;

   @Sync(initDefault=true)
   String orderError;

   /** Property name to error display string */
   @Sync(initDefault=true)
   Map<String,String> propErrors = null;

   @Sync(initDefault=true)
   boolean billToShipping;
   billToShipping =: billToShippingChanged();

   void clearErrors() {
      orderError = null;
      propErrors = null;
   }

}
