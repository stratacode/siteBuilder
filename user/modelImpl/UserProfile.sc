UserProfile {
   static UserProfile findByAuthToken(String tokenStr) {
      UserAuthToken authToken = UserAuthToken.findByToken(tokenStr);
      if (authToken != null) {
         Date now = new Date();
         if (authToken.expireDate != null && authToken.expireDate.getTime() < now.getTime()) {
            System.err.println("*** Auth token expired");
            authToken.dbDelete(false);
            return null;
         }
         return authToken.user;
      }
      return null;
   }

   String createAuthToken() {
      Date expireDate = new Date(System.currentTimeMillis() + userbase.authTokenDurationMillis);
      UserAuthToken newToken = new UserAuthToken();
      newToken.createdDate = new Date();
      newToken.user = this;
      newToken.expireDate = expireDate;
      //newToken.token = UUID.randomUUID().toString();
      // More compact and with more bits of randomness
      newToken.token = DBUtil.createSecureUniqueToken();
      newToken.dbInsert(true);
      return newToken.token;
   }

   void deleteAuthToken(UserProfile user, String tokenStr) {
      UserAuthToken token = UserAuthToken.findByToken(tokenStr);
      try {
         if (token != null && token.user == user)
            token.dbDelete(false);
         else
            System.err.println("*** Warning - did not find token to delete");
      }
      catch (IllegalArgumentException exc) {
         DBUtil.error("Failed to delete token: " + tokenStr);
      }
   }

   Locale getLocale() {
      if (localeTag == null)
         return null; 
      if (locale == null)
         locale = new Locale(localeTag);
      return locale;
   }

   UserProfileStats getOrCreateStats() {
      if (userProfileStats == null) {
         userProfileStats = new UserProfileStats();
         userProfileStats.userProfile = this;
         //userProfileStats.dbInsert();
      }
      return userProfileStats;
   }

   void loginSuccess(String remoteIp) {
      if (userbase.recordStats) {
         UserProfileStats stats = getOrCreateStats();
         String prevRemoteIp = stats.lastRemoteIp;
         stats.lastRemoteIp = remoteIp;
         stats.loginSuccess();
         profileEvent("loginSuccess", prevRemoteIp, remoteIp, stats.loginCt);
      }
   }

   void loginFailed(String remoteIp) {
      if (userbase.recordStats) {
         UserProfileStats stats = getOrCreateStats();
         String prevRemoteIp = remoteIp;
         stats.lastRemoteIp = remoteIp;
         stats.loginFailed();
         profileEvent("loginFailed", prevRemoteIp, remoteIp, stats.loginFailedCt);
      }
   }

   void profileEvent(String eventName, String prevRemoteIp, String newRemoteIp, int count) {
      // Adding a new event only when the remoteIp has changed since we keep counts at a higher level
      if (userbase.recordEvents && !DynUtil.equalObjects(prevRemoteIp, newRemoteIp)) {
         UserProfileStats stats = userProfileStats;
         List<UserProfileEvent> events = stats.profileEvents;
         if (events == null) {
            events = new ArrayList<UserProfileEvent>();
            stats.profileEvents = events;
         }
         if (events.size() > userbase.maxProfileEvents) {
            events.remove(0);
         }
         events.add(new UserProfileEvent("loginFailed", newRemoteIp, count));
      }
   }

   static String getHashedPassword(UserProfile user, String password) {
      String salt = user.salt;
      return DBUtil.hashPassword(salt, password);
   }
}
