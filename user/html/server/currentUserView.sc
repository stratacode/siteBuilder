import java.util.Locale;
import javax.servlet.http.Cookie;
import sc.lang.html.WebCookie;

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
         String remoteIp = ctx.getRemoteIp();
         this.remoteIp = remoteIp;
         this.acceptLanguage = req.getHeader("Accept-Language");
         this.userAgent = ctx.getUserAgent();
         this.referrer = ctx.getReferrer();
         if (userbase != null) {
            Cookie cookie = ctx.getCookie(userbase.cookieName);
            if (cookie != null)
               this.userAuthToken = cookie.getValue();
         }

         Window window = ctx.windowCtx.window;
         this.userAgentInfo = window.userAgentInfo;

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
         String remoteIp = ctx.getRemoteIp();
         if (this.remoteIp == null || !this.remoteIp.equals(remoteIp)) {
            System.err.println("*** Ip address changed for UserView");
            clearSession();
            return;
         }
         if (userbase != null) {
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
      Window window = Window.window;
      if (window == null || userbase == null) // There's no window available in the current context.
         return;
      WebCookie cookie = new WebCookie(cookieName, value);
      if (userbase.secureCookie)
         cookie.secure = true;
      if (userbase.cookieDomain != null)
         cookie.domain = userbase.cookieDomain;
      if (userbase.cookiePath != null)
         cookie.path = userbase.cookiePath;
      if (value.length() == 0)
         cookie.maxAgeSecs = 0;
      else
         cookie.maxAgeSecs = userbase.cookieDurationSeconds;
      window.addCookie(cookie);
   }

   void persistAuthToken(String token) {
      addUserCookie(userbase.cookieName, token);
   }
   void clearAuthToken() {
      persistAuthToken(""); // Clear our cookie
      Window win = Window.getWindow();
      if (win != null) // Clear the session on the next chance
         win.invalidateSession();
   }
}
