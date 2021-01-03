import sc.lang.html.Window;

abstract class WindowEvent extends SessionEvent {
   int durationMillis;

   transient Window window;

   void windowClosed() {
      long now = System.currentTimeMillis();
      if (eventTime != null) {
         long dur = now - eventTime.getTime();
         if (dur < Integer.MAX_VALUE && dur > 0) {
            durationMillis = (int) dur;
         }
         else {
            System.err.println("*** Invalid duration for window event: " + dur);
         }
      }
   }

   String getDurationStr() {
      return durationMillis == 0 ? "" : TextUtil.formatDuration(durationMillis);
   }
}
