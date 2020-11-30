@Component @Sync(onDemand=true)
scope<window>
class OrderView {
   Storefront store;
   UserView userView;

   @Sync(initDefault=true)
   int numLineItems;

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

   boolean validAddress = false;
   boolean editAddress = true;

   boolean validPayment = false;
   boolean editPayment = true;

   /** Set to true after submitting the order for registered users if the address is not the 'homeAddress' */
   boolean confirmDefaultAddress = false;
   boolean confirmDefaultPayment = false;

   boolean newOrderSubmitted = false;
   boolean saveOrderPaymentInfo = false;

   boolean showLoginView = false;

   OrderView(Storefront store, UserView userView) {
      this.store = store;
      this.userView = userView;
      userView.orderView = this;
   }

   void clearErrors() {
      orderError = null;
      propErrors = null;
   }
}
