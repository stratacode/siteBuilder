@Sync
class SearchResultGroup {
   String resultTypeName;

   List<SearchResultEntry> results = new ArrayList<SearchResultEntry>();

   int priority;

   SearchResultGroup(String resultTypeName, int priority) {
      this.resultTypeName = resultTypeName;
      this.priority = priority;
   }
}
