@Sync(onDemand=true)
@DBTypeSettings
class UserSession {
   @FindBy(orderBy="-lastEventTime", paged=true)
   UserProfile user;     // when tracking anonymous users with cookies, points back to the user-id

   @Sync(syncMode=SyncMode.Disabled)
   String userMarker; // hash with userbase.salt + siteName, remoteIp + user-agent to identify unique users without cookies

   // Replace this with userMarker - so we always have a sessionId, even if it's not as accurate as the user.id based on the cookie
   String sessionMarker; // Hash-token to refer to this session for logging and debugging

   @Sync(initDefault=true)
   Date createTime;

   @Sync(initDefault=true)
   Date lastEventTime;

   @DBPropertySettings(columnType="jsonb")
   @Sync(initDefault=true)
   List<SessionEvent> sessionEvents;

   @Sync(initDefault=true)
   String remoteIp;

   @Sync(initDefault=true)
   String referrer; // cleaned up url from http header

   @Sync(initDefault=true)
   String referrerSource; // general name for origin of this session - commonly used are query params: utm_source, source, ref

   @Sync(initDefault=true)
   String browser;

   @Sync(initDefault=true)
   String browserVersion;

   @Sync(initDefault=true)
   String osName;

   @Sync(initDefault=true)
   String countryCode;

   @Sync(initDefault=true)
   String postalCode;

   @Sync(initDefault=true)
   String cityName;

   @Sync(initDefault=true)
   String timezone;

   @Sync(initDefault=true)
   int screenWidth, screenHeight;

   String getEventTimeDisplay(int ix) {
      if (ix == -1 || sessionEvents == null || ix >= sessionEvents.size())
         return "<invalid>";
      if (ix == 0)
         return TextUtil.formatUserDate(sessionEvents.get(0).eventTime, true);
      else
         return PTypeUtil.getTimeDelta(sessionEvents.get(ix - 1).eventTime.getTime(),
                                       sessionEvents.get(ix). eventTime.getTime());
   }

   String getDeviceType() {
      if (screenWidth == sc.lang.html.Window.DefaultWidth)
         return "Unknown";
      if (screenWidth < 580)
         return "Mobile";
      else if (screenWidth < 992)
         return "Tablet";
      else if (screenWidth < 1440)
         return "Laptop";
      else
         return "Desktop";
   }
}
