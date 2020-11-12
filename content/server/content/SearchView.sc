SearchView {
   void doSearch(String value) {
      searchResult = siteView.doSearch(siteView, value, searchStartIx, searchMaxNum);
      if (searchResult == null || searchResult.totalResults == 0)
         resultsMessage = "No results found for search";
      else
         resultsMessage = "Search results - found: ";
      searchText = value;
   }

   void clearSearch() {
      searchResult = null;
      resultsMessage = null;
      searchVisible = false;
      searchText = "";
   }
}
