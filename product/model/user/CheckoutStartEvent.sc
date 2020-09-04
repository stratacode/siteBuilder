class CheckoutStartEvent extends SessionEvent {
   String getEventName() {
      return "checkout start";
   }

   String getEventDetail() {
      return "";
   }

   String getEventTarget(SiteContext site) {
      return null;
   }

   boolean getHasLink() {
      return false;
   }
}
