import java.util.Arrays;

class StoreSearchProvider implements ISearchProvider {
   int catDefPriority = 2;
   int productDefPriority = 3;

   List<SearchResult> doSearch(String searchText, SiteContext site, int startIx, int num) {
      if (site != null && !(site instanceof Storefront))
         return null;
      Storefront store = (Storefront) site;
      List<String> storePropNames = store == null ? null : Arrays.asList("store");
      List<Object> storePropVals = Arrays.asList(store);
      List<Category> catItems = (List<Category>) Category.getDBTypeDescriptor().searchQuery(searchText,
                           storePropNames, storePropVals, null, null, startIx, num);
      List<Product> prodItems = (List<Product>) Product.getDBTypeDescriptor().searchQuery(searchText,
                           storePropNames, storePropVals, null, null, startIx, num);
      int numCats = catItems == null ? 0 : catItems.size(),
          numProds = prodItems == null ? 0 : prodItems.size();
      int total = numCats + numProds;
      ArrayList<SearchResult> res = total == 0 ? null : new ArrayList<SearchResult>(total);

      for (int i = 0; i < numCats; i++) {
         Category cat = catItems.get(i);
         SearchResult sr = new SearchResult(this, "category", cat.name, cat.mainMedia, cat.pageUrl, cat.shortDesc, catDefPriority);
         res.add(sr);
      }
      for (int i = 0; i < numProds; i++) {
         Product prod = prodItems.get(i);
         SearchResult sr = new SearchResult(this, "product", prod.name, prod.mainMedia, prod.pageUrl, prod.shortDesc, catDefPriority);
         res.add(sr);
      }
      return res;
   }
}