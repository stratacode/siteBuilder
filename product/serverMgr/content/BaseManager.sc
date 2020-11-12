import java.util.TreeSet;
import java.util.Arrays;

BaseManager {
   static final List<String> searchOrderBy = Arrays.asList("-lastModified");
   static final List<String> searchStore = Arrays.asList("store");
   static final List<String> searchStoreRecent = Arrays.asList("store","recentlyModified");

   final static int recentDays = 2;
   final static int recentMillis = recentDays * 24 * 60 * 60 * 1000;

   store =: storeChanged();

   void storeChanged() {
      resetForm();
   }

   List<Object> getSearchStoreValues() {
      ArrayList<Object> res = new ArrayList<Object>();
      res.add(store);
      return res;
   }

   List<Object> getSearchStoreRecentValues() {
      ArrayList<Object> res = new ArrayList<Object>();
      res.add(store);
      res.add(true);
      return res;
   }

}
