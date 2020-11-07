import java.util.Arrays;
import java.util.Collections;

SiteView {
   static List<ISearchProvider> searchProviders = new ArrayList<ISearchProvider>(Arrays.asList(new PageSearchProvider()));

   pathName =: validateSite();

   void start() {
      validateSite();
   }

   void validateSite() {
      SiteContext ctx = SiteContext.findBySitePathName(pathName);
      if (ctx == null) {
         System.err.println("*** No site with pathname: " + pathName);
      }
      else
         this.siteContext = ctx;
   }

   void pageVisited() {
      pageVisitCount++;
      // Each time the page is loaded, need to check and see if the auth token still matches in the request, update
      // the state of the UserView's loggedIn property
      currentUserView.refresh();
      currentUserView.addPageEvent(siteContext, pathName);
   }

   /*
   PageView getPageView(String pathName) {
      List<PageDef> pageDefs;
      if (pathName == null)
         pageDefs = PageDef.findByHomePage(true, siteContext);
      else
         pageDefs = PageDef.findByPagePathName(pathName, siteContext);
      if (pageDefs == null || pageDefs.size() == 0)
         return new PageView(this, null, pathName);
      return new PageView(this, pageDefs.get(0), pathName);
   }
      */

   static SearchResult doSearch(SiteView siteView, String searchText, int startIx, int maxNum) {
      SiteContext siteContext = siteView == null ? null : siteView.siteContext;

      SearchResult res = new SearchResult();

      for (ISearchProvider sp:searchProviders) {
         sp.addToSearch(res, searchText, siteContext, startIx, maxNum);
      }
      return res;
   }
}
