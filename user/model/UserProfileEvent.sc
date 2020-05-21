class UserProfileEvent {
   String eventName;
   String remoteIp;
   int eventCount;

   UserProfileEvent(String eventName, String remoteIp, int eventCount) {
      this.eventName = eventName;
      this.remoteIp = remoteIp;
      this.eventCount = eventCount;
   }

   UserProfileEvent() {}
}
