import java.util.Arrays;

BaseManager {
   static final List<String> searchOrderBy = Arrays.asList("-lastModified");
   static final List<String> searchSite = Arrays.asList("site");
   static final List<String> searchSiteRecent = Arrays.asList("site","recentlyModified");

   final static int recentDays = 2;
   final static int recentMillis = recentDays * 24 * 60 * 60 * 1000;

   List<Object> getSearchSiteValues() {
      ArrayList<Object> res = new ArrayList<Object>();
      res.add(site);
      return res;
   }
}
