import java.util.Locale;
import javax.servlet.http.Cookie;

// TODO: this should be per-user manager where we pull the user manager out of a
// a global Site object (broken out of Storefront)
currentUserView {
   void init() {
      Context ctx = Context.getCurrentContext();
      HttpServletRequest req = ctx.request;
      if (req == null) {
         // Happens during session expiration
         userViewError = "User view initialized without a request";
         clearSession();
      }
      else {
         String remoteIp = req.getHeader("X-Forwarded-For");
         if (remoteIp == null)
            remoteIp = req.getRemoteAddr();
         this.remoteIp = remoteIp;
         this.acceptLanguage = req.getHeader("Accept-Language");
         this.userAgent = req.getHeader("User-agent");
         Cookie cookie = ctx.getCookie(userbase.cookieName);
         if (cookie != null)
            this.userAuthToken = cookie.getValue();

         HttpSession session = req.getSession(false);
         if (session != null) {
            sessionMarker = (String) session.getAttribute("_sc_sessionMarker");
            if (sessionMarker == null)
               sessionMarker = DBUtil.createMarkerToken();
            session.setAttribute("_sc_sessionMarker", sessionMarker);
         }
      }

      super.init();

      if (user != null && req != null) {
         Locale locale = user.locale;
         if (locale == null) {
            locale = req.getLocale();
            user.localeTag = locale.toLanguageTag();
         }

         initFromContext(ctx);
      }
   }

   // Hook for other cookies or request state initialization
   void initFromContext(Context ctx) {
   }

   void refresh() {
      Context ctx = Context.getCurrentContext();
      HttpServletRequest req = ctx.request;
      if (req == null) {
         userViewError = "User view refreshed without a request";
         clearSession();
      }
      else {
         String remoteIp = req.getHeader("X-Forwarded-For");
         if (remoteIp == null)
            remoteIp = req.getRemoteAddr();
         if (this.remoteIp == null || !this.remoteIp.equals(remoteIp)) {
            System.err.println("*** Ip address changed for UserView");
            clearSession();
            return;
         }
         Cookie cookie = ctx.getCookie(userbase.cookieName);
         if (cookie != null) {
            String newToken = cookie.getValue();
            if (this.userAuthToken == null || !newToken.equals(this.userAuthToken)) {
               this.userAuthToken = newToken;
               super.refresh();
            }
         }
      }
   }

   void clearSession() {
      user = null;
      remoteIp = null;
      acceptLanguage = null;
      userAgent = null;
      userAuthToken = null;
      userName = null;
      password = null;
      loginStatus = LoginStatus.NotLoggedIn;
   }

   void addUserCookie(String cookieName, String value) {
      Context ctx = Context.getCurrentContext();
      HttpServletResponse resp = ctx.response;
      if (resp == null || resp.isCommitted()) {
         System.err.println("*** Warning - failed to add request cookie: " + cookieName);
         return;
      }
      Cookie cookie = new Cookie(cookieName, value);
      if (userbase.secureCookie)
         cookie.setSecure(true);
      if (userbase.cookieDomain != null)
         cookie.setDomain(userbase.cookieDomain);
      if (userbase.cookiePath != null)
         cookie.setPath(userbase.cookiePath);
      if (value.length() == 0)
         cookie.setMaxAge(0);
      else
         cookie.setMaxAge(userbase.cookieDurationSeconds);
      resp.addCookie(cookie);
   }

   void persistAuthToken(String token) {
      addUserCookie(userbase.cookieName, token);
   }
   void clearAuthToken() {
      persistAuthToken(""); // Clear our cookie
      Context ctx = Context.getCurrentContext();
      if (ctx != null)
         ctx.markSessionInvalid();
   }
}
