import sc.lang.html.Window;

abstract class WindowEvent extends SessionEvent {
   int durationMillis;
   boolean expired;

   transient Window window;

   void windowClosed(boolean sessionExpired) {
      long now = System.currentTimeMillis();
      if (eventTime != null && !sessionExpired) {
         long dur = now - eventTime.getTime();
         if (dur < Integer.MAX_VALUE && dur > 0) {
            durationMillis = (int) dur;
         }
         else {
            System.err.println("*** Invalid duration for window event: " + dur);
         }
      }
      expired = sessionExpired;
   }

   String getDurationStr() {
      return durationMillis == 0 ? "" : TextUtil.formatDuration(durationMillis);
   }
}
