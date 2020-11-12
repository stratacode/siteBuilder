class SearchView {
   @Sync(initDefault=true, resetState=true)
   String searchText;
   @Sync(initDefault=true, resetState=true)
   boolean searchVisible;
   SiteView siteView;

   @Sync(initDefault=true, resetState=true)
   SearchResult searchResult;
   int searchStartIx = 0;
   int searchMaxNum = 20;

   @Sync(initDefault=true, resetState=true)
   String resultsMessage = null;

}
