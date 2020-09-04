@DBTypeSettings
class UserProfileStats {
   @DBPropertySettings(reverseProperty="userProfileStats", required=true)
   UserProfile userProfile;

   Date registered;
   Date lastLogin;
   Date lastModified;
   Date lastLoginFailed;
   Date lastActivity;

   int loginCt;
   int loginFailedCt;

   String lastRemoteIp;

   // We record some fixed number of these each time the remoteIp changes
   List<UserProfileEvent> profileEvents;

   int numSessionEvents;

   void notifySessionEvent(SessionEvent event) {
      lastActivity = event.eventTime;
      numSessionEvents++;
   }

   void loginSuccess() {
      lastLogin = new Date();
      lastActivity = lastLogin;
      loginCt++;
   }

   void loginFailed() {
      lastLoginFailed = new Date();
      loginFailedCt++;
   }

   void addProfileEvent(UserProfileEvent event) {
      List<UserProfileEvent> events = profileEvents;
      if (events == null) {
         events = new ArrayList<UserProfileEvent>();
         profileEvents = events;
      }
      if (events.size() > userProfile.userbase.maxProfileEvents) {
         events.remove(0);
      }
      lastActivity = event.time;
      events.add(event);
   }
}
