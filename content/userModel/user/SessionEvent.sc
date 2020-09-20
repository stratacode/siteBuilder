@Sync(onDemand=true)
abstract class SessionEvent {
   Date eventTime;
   public SessionEvent() {
      eventTime = new Date();
   }

   // Name of the event
   abstract String getEventName();

   // Link to the page, product, etc. involved
   abstract String getEventTarget(SiteContext site);

   // Info specific to the event
   abstract String getEventDetail();

   boolean getHasLink() {
      return true;
   }

   String toString() {
      return getEventName() + " " + getEventDetail();
   }
}