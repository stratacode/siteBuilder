interface ISearchProvider {
   List<SearchResult> doSearch(String searchText, SiteContext site, int startIx, int num);
}