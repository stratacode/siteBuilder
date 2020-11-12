import java.util.Arrays;

class BlogSearchProvider implements ISearchProvider {
   int categoryPriority = 2;
   int postPriority = 3;

   void addToSearch(SearchResult result, String searchText, SiteContext site, int startIx, int num) {
      List<String> sitePropNames = site == null ? null : Arrays.asList("site");
      List<Object> sitePropVals = Arrays.asList(site);
      List<BlogCategory> catItems = (List<BlogCategory>) BlogCategory.getDBTypeDescriptor().searchQuery(searchText,
                           sitePropNames, sitePropVals, null, null, startIx, num);
      List<BlogPost> postItems = (List<BlogPost>) BlogPost.getDBTypeDescriptor().searchQuery(searchText,
                           sitePropNames, sitePropVals, null, null, startIx, num);
      int numCats = catItems == null ? 0 : catItems.size(),
          numPosts = postItems == null ? 0 : postItems.size();
      int total = numCats + numPosts;
      ArrayList<SearchResult> res = total == 0 ? null : new ArrayList<SearchResult>(total);

      if (numCats > 0) {
         SearchResultGroup catGroup = new SearchResultGroup("Blog categories", categoryPriority);

         for (int i = 0; i < numCats; i++) {
            BlogCategory cat = catItems.get(i);
            SearchResultEntry sr = new SearchResultEntry(catGroup, cat.name, cat.mainMedia, cat.pageUrl, cat.shortDesc, null,
                                                         categoryPriority);
            catGroup.results.add(sr);
         }
         result.addGroup(catGroup);
      }

      if (numPosts > 0) {
         SearchResultGroup postGroup = new SearchResultGroup("Blog posts", postPriority);

         for (int i = 0; i < numPosts; i++) {
            BlogPost post = postItems.get(i);
            SearchResultEntry sr = new SearchResultEntry(postGroup, post.name, post.mainMedia, post.pageUrl, post.shortDesc,
                                               "", postPriority);
            postGroup.results.add(sr);
         }
         result.addGroup(postGroup);
      }
   }
}
