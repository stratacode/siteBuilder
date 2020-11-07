import java.util.Arrays;

class PageSearchProvider implements ISearchProvider {

   int pageDefPriority = 1;

   static List<String> siteProps = Arrays.asList("site");

   List<SearchResult> doSearch(String searchText, SiteContext site, int startIx, int num) {
      List<PageDef> searchItems = (List<PageDef>) PageDef.getDBTypeDescriptor().searchQuery(searchText,
          site == null ? null : siteProps, site == null ? null : Arrays.asList(site), null, null, startIx, num);
      int numResults;
      if (searchItems == null || (numResults = searchItems.size()) == 0)
         return null;
      ArrayList<SearchResult> res = new ArrayList<SearchResult>(numResults);
      for (int i = 0; i < numResults; i++) {
         PageDef pageDef = searchItems.get(i);
         SearchResult sr = new SearchResult(this, "page", pageDef.pageName, null, pageDef.pageUrl, null, null, pageDefPriority);
         res.add(sr);
      }
      return res;
   }
}