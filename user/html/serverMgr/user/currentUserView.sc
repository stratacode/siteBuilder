import javax.servlet.http.Cookie;

currentUserView {
   final static String cookieName = "scSBSite";

   void initFromContext(Context ctx) {
      Cookie cookie = ctx.getCookie(cookieName);
      if (cookie != null) {
         String lastSiteName = cookie.getValue();
         if (lastSiteName != null) {
            SiteContext site = SiteContext.findBySitePathName(lastSiteName);
            if (site != null) {
               lastSite = site;
            }
            else {
               System.out.println("*** Cookie sbSite - no site found with path: lastSite");
               addUserCookie(cookieName, "");
            }
         }
      }
   }

   void changeLastSite(SiteContext newSite) {
      super.changeLastSite(newSite);
      addUserCookie(cookieName, newSite.sitePathName);
   }
}
