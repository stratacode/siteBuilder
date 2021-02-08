import sc.lang.html.Window;

abstract class WindowEvent extends SessionEvent {
   int durationMillis;
   boolean expired;
   int windowId;

   void windowClosed(boolean sessionExpired) {
      long now = System.currentTimeMillis();
      if (eventTime != null && !sessionExpired) {
         long dur = now - eventTime.getTime();
         if (dur < Integer.MAX_VALUE && dur > 0) {
            durationMillis = (int) dur;
         }
         // We have a zero duration event when a close window comes in for an expired session - so the event is
         // created and closed at the same time.
         else if (dur < 0) {
            System.err.println("*** Invalid duration for window event: " + dur);
         }
      }
      expired = sessionExpired;
   }

   void setDurationMillis(int duration) {
      if (duration == this.durationMillis)
         return;
      this.durationMillis = duration;
      Bind.sendChangedEvent(this, "durationStr");
   }

   String getDurationStr() {
      return durationMillis == 0 ? "" : TextUtil.formatDuration(durationMillis);
   }
}
