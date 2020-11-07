class SearchResult {
   int totalResults; 
   List<SearchResultGroup> resultGroups;

   void addGroup(SearchResultGroup group) {
      int ix = -1;

      totalResults += group.results.size();

      if (resultGroups == null)
         resultGroups = new ArrayList<SearchResultGroup>();
      else {
         for (int i = 0; i < resultGroups.size(); i++) {
            SearchResultGroup og = resultGroups.get(i);
            if (og.priority < group.priority) {
               resultGroups.add(i, group);
               return;
            }
         }
      }
      resultGroups.add(group);
   }
}
