class SearchResult implements Comparable {
   ISearchProvider provider;
   String resultTypeName;
   String name;
   ManagedMedia media;
   String url;
   String shortDesc;
   int searchPriority;

   SearchResult(ISearchProvider provider, String resultTypeName, String name, ManagedMedia media,
                String url, String shortDesc, int priority) {
      this.provider = provider;
      this.resultTypeName = resultTypeName;
      this.name = name;
      this.media = media;
      this.url = url;
      this.shortDesc = shortDesc;
      this.searchPriority = priority;
   }

   // TODO: add facet information to help categorize the search results, and refine the search process.
   // For gathering facets, it seems the initial search results across each provider will determine the
   // set of facets used. Initially, we could just get enough search results to fill up the facets, or
   // maybe have another way to have cached facet result counts that we can filter based on the search pattern?

   int compareTo(Object otherObj) {
      SearchResult other = (SearchResult) otherObj;
      return searchPriority - other.searchPriority;
   }

   String getImageUrl() {
      return media == null ? "/icons/file-text40.svg" : media.getUrlForSize(200);
   }

   String getImageSrcSet() {
      return media == null ? null : media.getSrcSetForSize(200);
   }
}