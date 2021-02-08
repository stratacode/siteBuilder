import sc.lang.html.Window;
import sc.util.StringUtil;

UserSession {
   // TODO: these next two values are on the low-side for easier testing
   // Because we are caching these in memory, if the server dies we'll lose this history
   final static long idleSaveTime = 10*1000;

   // After how much inactivity do we consider it a new session?
   final static long expireTime = 10*60*1000;

   transient boolean changedSession = false;

   void addPageEvent(String pathName, int windowId) {
      PageEvent event = new PageEvent();
      event.pathName = pathName;
      event.windowId = windowId;
      addSessionEvent(event);
   }

   PageEvent getPageEvent(String pathName, int winId) {
      if (sessionEvents == null)
         return null;
      for (int i = 0; i < sessionEvents.size(); i++) {
         SessionEvent sessionEvent = sessionEvents.get(i);
         if (sessionEvent instanceof PageEvent) {
            PageEvent pe = (PageEvent) sessionEvent;
            if (StringUtil.equalStrings(pe.pathName, pathName) && pe.windowId == winId)
               return pe;
         }
      }
      return null;
   }

   void addSessionEvent(SessionEvent event) {
      if (event instanceof WindowEvent) {
         WindowEvent wevent = (WindowEvent) event;
         Window win = Window.window;
         if (wevent.windowId == 0) {
            wevent.windowId = win == null ? -1 : win.windowId;
            if (wevent.windowId == -1)
               System.err.println("*** No window for event!");
         }
      }
      if (sessionEvents == null)
         sessionEvents = new BArrayList<SessionEvent>();
      if (sessionEvents.size() < user.userbase.maxSessionEvents)
         sessionEvents.add(event);
      user.getOrCreateStats().notifySessionEvent(event);
      Date eventTime = event.eventTime;
      if (lastEventTime == null || eventTime.getTime() > lastEventTime.getTime())
         lastEventTime = eventTime;
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
      long now = System.currentTimeMillis();
      for (SessionEvent ev:sessionEvents) {
         if (ev instanceof PageEvent) {
            PageEvent pv = (PageEvent) ev;
            int evWinId = pv.windowId;
            if (evWinId != -1 && evWinId == windowId) {
               pv.durationMillis = (int) (now - ev.eventTime.getTime());
               pv.scrollDepth = sd;
            }
         }
      }
      lastEventTime = new Date(now);
   }

}
