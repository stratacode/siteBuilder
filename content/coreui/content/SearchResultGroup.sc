class SearchResultGroup {
   ISearchProvider provider;
   String resultTypeName;

   List<SearchResultEntry> results = new ArrayList<SearchResultEntry>();

   int priority;

   SearchResultGroup(ISearchProvider provider, String resultTypeName, int priority) {
      this.provider = provider;
      this.resultTypeName = resultTypeName;
      this.priority = priority;
   }
}
