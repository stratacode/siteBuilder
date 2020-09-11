Category {
   void validateAllProducts() {
      List<Product> newList = null;
      if (childProducts != null) {
         if (linkedProducts == null && productQuery == null) {
            allProducts = childProducts;
            return;
         }
         else {
            newList = new BArrayList<Product>();
            newList.addAll(childProducts);
            if (linkedProducts != null)
               newList.addAll(linkedProducts);
         }
      }
      else if (linkedProducts != null) {
         if (productQuery == null) {
            allProducts = linkedProducts;
            return;
         }
         else {
            newList = new BArrayList<Product>();
            newList.addAll(linkedProducts);
         }
      }

      if (productQuery != null) {
         List<Product> dbList = (List<Product>) Product.getDBTypeDescriptor().query(productQuery, null, null, -1, -1);
         if (newList == null)
            newList = dbList;
         else
            newList.addAll(dbList);
      }
      allProducts = newList;
   }
}