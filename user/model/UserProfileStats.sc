@DBTypeSettings
class UserProfileStats {
   @DBPropertySettings(reverseProperty="userProfileStats")
   UserProfile userProfile;

   Date registered;
   Date lastLogin;
   Date lastModified;
   Date lastLoginFailed;

   int loginCt;
   int loginFailedCt;

   String lastRemoteIp;

   // We record some fixed number of these each time the remoteIp changes
   List<UserProfileEvent> profileEvents;

   void loginSuccess() {
      lastLogin = new Date();
      loginCt++;
   }

   void loginFailed() {
      lastLoginFailed = new Date();
      loginFailedCt++;
   }
}
