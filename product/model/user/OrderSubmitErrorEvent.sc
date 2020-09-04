class OrderSubmitErrorEvent extends SessionEvent {
   String error;

   OrderSubmitErrorEvent(String e) {
      error = e;
   }
   OrderSubmitErrorEvent() {
   }

   String getEventName() {
      return "order submit error";
   }

   String getEventDetail() {
      return error;
   }

   boolean getHasLink() {
      return false;
   }

   String getEventTarget(SiteContext site) {
      return null;
   }
}
