@DBTypeSettings
class UserSession {
   @FindBy
   UserProfile user;

   @FindBy(paged=true, orderBy="-lastModified")
   SiteContext site;

   String sessionMarker;
   Date createTime;
   Date lastModified;

   @DBPropertySettings(columnType="jsonb")
   List<SessionEvent> sessionEvents;

   void addPageEvent(String pathName) {
      PageEvent event = new PageEvent();
      event.pathName = pathName;
      addSessionEvent(event);
   }

   void addSessionEvent(SessionEvent event) {
      if (sessionEvents == null)
         sessionEvents = new BArrayList<SessionEvent>();
      if (sessionEvents.size() < user.userbase.maxSessionEvents)
         sessionEvents.add(event);
      user.getOrCreateStats().notifySessionEvent(event);

      // TODO: this can be done offline in a batch rather than immediately for more throughput and to reduce the
      // number of writes for a given session. Maybe we create a transaction to hold all of these changes and run
      // it using PTypeUtil.addScheduledJob
      if (getDBObject().isTransient())
         dbInsert(true);
      else
         dbUpdate();
   }

   String getEventTimeDisplay(int ix) {
      if (ix == 0)
         return TextUtil.formatUserDate(sessionEvents.get(0).eventTime, true);
      else
         return PTypeUtil.getTimeDelta(sessionEvents.get(ix - 1).eventTime.getTime(),
                                       sessionEvents.get(ix). eventTime.getTime());
   }
}
