import java.util.TreeSet;
import java.util.Arrays;

BaseManager {
   static final List<String> searchOrderBy = Arrays.asList("-lastModified");
   static final List<String> searchStore = Arrays.asList("store");

   List<Object> getSearchStoreValues() {
      ArrayList<Object> res = new ArrayList<Object>();
      res.add(store);
      return res;
   }

   static List<Category> getMatchingCategories(Storefront store, String pattern) {
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<Category> res = new ArrayList<Category>();
      ArrayList<Object> searchValues = new ArrayList<Object>();
      searchValues.add(store);
      List<Category> allMatches = (List<Category>) Category.getDBTypeDescriptor().searchQuery(pattern, searchStore, searchValues, null, searchOrderBy, 0, 20);
      for (Category match:allMatches) {
         if (!found.contains(match.pathName)) {
            res.add(match);
            found.add(match.pathName);
            if (res.size() == 10)
               break;
         }
      }
      return res;
   }

   static List<Product> getMatchingProducts(Storefront store, String pattern) {
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<Product> res = new ArrayList<Product>();
      ArrayList<Object> searchValues = new ArrayList<Object>();
      searchValues.add(store);
      List<Product> allMatches = (List<Product>) Product.getDBTypeDescriptor().searchQuery(pattern, searchStore, searchValues, null, searchOrderBy, 0, 20);
      for (Product match:allMatches) {
         if (!found.contains(match.pathName)) {
            res.add(match);
            found.add(match.pathName);
            if (res.size() == 10)
               break;
         }
      }
      return res;
   }

}
