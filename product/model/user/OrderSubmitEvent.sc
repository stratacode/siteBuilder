@Sync(onDemand=true)
class OrderSubmitEvent extends SessionEvent {
   String getEventName() {
      return "order submit";
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
