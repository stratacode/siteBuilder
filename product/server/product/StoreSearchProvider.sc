import java.util.Arrays;

class StoreSearchProvider implements ISearchProvider {
   int categoryPriority = 2;
   int productPriority = 3;

   void addToSearch(SearchResult result, String searchText, SiteContext site, int startIx, int num) {
      if (site != null && !(site instanceof Storefront))
         return;
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

      if (numCats > 0) {
         SearchResultGroup catGroup = new SearchResultGroup(this, "Categories", categoryPriority);

         for (int i = 0; i < numCats; i++) {
            Category cat = catItems.get(i);
            SearchResultEntry sr = new SearchResultEntry(catGroup, cat.name, cat.mainMedia, cat.pageUrl, cat.shortDesc, null,
                                               categoryPriority);
            catGroup.results.add(sr);
         }
         result.addGroup(catGroup);
      }

      if (numProds > 0) {
         SearchResultGroup prodGroup = new SearchResultGroup(this, "Products", productPriority);

         for (int i = 0; i < numProds; i++) {
            Product prod = prodItems.get(i);
            String priceHtml = prod.priceDisplayHtml;
            SearchResultEntry sr = new SearchResultEntry(prodGroup, prod.name, prod.mainMedia, prod.pageUrl, prod.shortDesc,
                                               priceHtml, productPriority);
            prodGroup.results.add(sr);
         }
         result.addGroup(prodGroup);
      }
   }
}