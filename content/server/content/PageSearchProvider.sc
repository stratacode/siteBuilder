import java.util.Arrays;

class PageSearchProvider implements ISearchProvider {

   int pageDefPriority = 1;

   static List<String> siteProps = Arrays.asList("site");

   void addToSearch(SearchResult result, String searchText, SiteContext site, int startIx, int num) {
      List<PageDef> searchItems = (List<PageDef>) PageDef.getDBTypeDescriptor().searchQuery(searchText,
          site == null ? null : siteProps, site == null ? null : Arrays.asList(site), null, null, startIx, num);
      int numResults;
      if (searchItems == null || (numResults = searchItems.size()) == 0)
         return;

      SearchResultGroup group = new SearchResultGroup("Pages", pageDefPriority);

      group.results = new ArrayList<SearchResultEntry>(numResults);
      for (int i = 0; i < numResults; i++) {
         PageDef pageDef = searchItems.get(i);
         SearchResultEntry sr = new SearchResultEntry(group, pageDef.pageName, null, pageDef.pageUrl, null, null, pageDefPriority);
         group.results.add(sr);
      }
      result.addGroup(group);
   }
}