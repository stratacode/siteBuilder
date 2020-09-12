import java.util.TreeSet;

Category {
   void validateAllProducts() {
      List<Product> newList = null;
      TreeSet<String> prodPaths = null;
      if (childProducts != null) {
         if (linkedProducts == null && productQuery == null) {
            allProducts = childProducts;
            return;
         }
         else {
            prodPaths = new TreeSet<String>();
            newList = new BArrayList<Product>();
            addAllProducts(newList, childProducts, prodPaths);
            if (linkedProducts != null)
               addAllProducts(newList, linkedProducts, prodPaths);
         }
      }
      else if (linkedProducts != null) {
         if (productQuery == null) {
            allProducts = linkedProducts;
            return;
         }
         else {
            newList = new BArrayList<Product>();
            prodPaths = new TreeSet<String>();

            addAllProducts(newList, linkedProducts, prodPaths);
         }
      }

      if (productQuery != null) {
         List<Product> dbList = (List<Product>) Product.getDBTypeDescriptor().query(productQuery, null, null, -1, -1);
         if (newList == null)
            newList = dbList;
         else {
            addAllProducts(newList, dbList, prodPaths);
         }
      }
      allProducts = newList;
   }

   // Avoid adding two products with the same path name for the same category
   private void addAllProducts(List<Product> newList, List<Product> prods, TreeSet<String> prodPaths) {
      for (Product prod:prods) {
         if (!prodPaths.contains(prod.pathName)) {
            prodPaths.add(prod.pathName);
            newList.add(prod);
         }
      }
   }
}