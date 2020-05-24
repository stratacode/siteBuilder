import java.util.Locale;
import javax.servlet.http.Cookie;

scope<appSession>
currentUserView {
   void init() {
      Context ctx = Context.getCurrentContext();
      HttpServletRequest req = ctx.request;
      String remoteIp = req.getHeader("X-Forwarded-For");
      if (remoteIp == null)
         remoteIp = req.getRemoteAddr();
      this.remoteIp = remoteIp;
      this.acceptLanguage = req.getHeader("Accept-Language");
      this.userAgent = req.getHeader("User-agent");
      Cookie cookie = ctx.getCookie(mgr.cookieName);
      if (cookie != null)
         this.userAuthToken = cookie.getValue();

      super.init();

      if (user != null) {
         Locale locale = user.locale;
         if (locale == null) {
            locale = req.getLocale();
            user.localeTag = locale.toLanguageTag();
         }
      }
   }

   void persistAuthToken(String token) {
      Context ctx = Context.getCurrentContext();
      HttpServletResponse resp = ctx.response;
      Cookie cookie = new Cookie(mgr.cookieName, token);
      if (mgr.secureCookie)
         cookie.setSecure(true);
      if (mgr.cookieDomain != null)
         cookie.setDomain(mgr.cookieDomain);
      if (mgr.cookiePath != null)
         cookie.setPath(mgr.cookiePath);
      if (token.length() == 0)
         cookie.setMaxAge(0);
      else
         cookie.setMaxAge(mgr.cookieDurationSeconds);
      resp.addCookie(cookie);
   }
   void clearAuthToken() {
      persistAuthToken(""); // Clear our cookie
      Context ctx = Context.getCurrentContext();
      if (ctx != null)
         ctx.markSessionInvalid();
   }
}
