@DBTypeSettings
class UserSession {
   UserProfile user;

   @FindBy
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
      sessionEvents.add(event);

      // TODO: this can be done offline in a batch rather than immediately for more throughput and to reduce the
      // number of writes for a given session. Maybe we create a transaction to hold all of these changes and run
      // it using PTypeUtil.addScheduledJob
      if (getDBObject().isTransient())
         dbInsert(true);
      else
         dbUpdate();
   }
}
