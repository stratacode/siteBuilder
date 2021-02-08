@Sync(onDemand=true)
@CompilerSettings(compiledOnly=true)
abstract class SessionEvent {
   Date eventTime;
   public SessionEvent() {
      eventTime = new Date();
   }

   // Name of the event
   abstract String getEventName();

   // Info specific to the event
   abstract String getEventDetail();

   boolean getHasLink() {
      return true;
   }

   String toString() {
      return getEventName() + " " + getEventDetail();
   }

   @Bindable(manual=true)
   String getDurationStr() {
      return "";
   }

   @Bindable(manual=true)
   String getScrollDepthStr() {
      return "";
   }

   void windowClosed(boolean sessionExpired) {}
}
