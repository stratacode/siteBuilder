@Sync(onDemand=true)
@DBTypeSettings
class UserSession {
   @FindBy
   UserProfile user;

   String sessionMarker;
   @Sync(initDefault=true)
   Date createTime;
   @Sync(initDefault=true)
   Date lastModified;

   @DBPropertySettings(columnType="jsonb")
   @Sync(initDefault=true)
   List<SessionEvent> sessionEvents;

   @Sync(initDefault=true)
   String remoteIp;

   String getEventTimeDisplay(int ix) {
      if (ix == -1 || sessionEvents == null || ix >= sessionEvents.size())
         return "<invalid>";
      if (ix == 0)
         return TextUtil.formatUserDate(sessionEvents.get(0).eventTime, true);
      else
         return PTypeUtil.getTimeDelta(sessionEvents.get(ix - 1).eventTime.getTime(),
                                       sessionEvents.get(ix). eventTime.getTime());
   }
}
