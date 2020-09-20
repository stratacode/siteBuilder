@Sync(onDemand=true)
class UserProfileEvent {
   String eventName;
   String remoteIp;
   int eventCount;
   Date time;

   UserProfileEvent(String eventName, String remoteIp, int eventCount) {
      this.eventName = eventName;
      this.remoteIp = remoteIp;
      this.eventCount = eventCount;
      this.time = new Date();
   }

   UserProfileEvent() {}
}
