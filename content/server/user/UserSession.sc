import sc.lang.html.Window;

UserSession {
   // TODO: these next two values are on the low-side for easier testing
   // Because we are caching these in memory, if the server dies we'll lose this history
   final static long idleSaveTime = 10*1000;

   // After how much inactivity do we consider it a new session?
   final static long expireTime = 10*60*1000;

   transient boolean changedSession = false;

   void addPageEvent(String pathName) {
      PageEvent event = new PageEvent();
      event.pathName = pathName;
      addSessionEvent(event);
   }

   void addSessionEvent(SessionEvent event) {
      if (event instanceof WindowEvent) {
         WindowEvent wevent = (WindowEvent) event;
         wevent.window = Window.window;
      }
      if (sessionEvents == null)
         sessionEvents = new BArrayList<SessionEvent>();
      if (sessionEvents.size() < user.userbase.maxSessionEvents)
         sessionEvents.add(event);
      user.getOrCreateStats().notifySessionEvent(event);
   }

   boolean needsSave() {
      if (sessionEvents != null) {
         long now = System.currentTimeMillis();
         long mostRecent = getMostRecentTime();
         if ((now - mostRecent) > idleSaveTime)
            return true;
      }
      return false;
   }

   boolean isExpired() {
      if (sessionEvents != null) {
         long now = System.currentTimeMillis();
         long mostRecent = getMostRecentTime();
         if ((now - mostRecent) > expireTime)
            return true;
      }
      return false;
   }

   long getMostRecentTime() {
      long mostRecent = -1;
      for (SessionEvent ev:sessionEvents) {
         long evTime = ev.eventTime.getTime();
         if (ev instanceof WindowEvent) {
            int duration = ((WindowEvent) ev).durationMillis;
            evTime += duration;
         }
         if (mostRecent == -1 || evTime > mostRecent)
            mostRecent = evTime;
      }
      return mostRecent;
   }

   void updateScrollDepth(int windowId, int sd) {
      if (sessionEvents == null)
         return;
      for (SessionEvent ev:sessionEvents) {
         if (ev instanceof PageEvent) {
            PageEvent pv = (PageEvent) ev;
            Window evWin = pv.window;
            if (evWin != null && evWin.windowId == windowId) {
               pv.durationMillis = (int) (System.currentTimeMillis() - ev.eventTime.getTime());
               pv.scrollDepth = sd;
            }
         }
      }
   }

}
