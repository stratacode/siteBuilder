ProductViewDef {
   String validateProductPathName(String path) {
      String err = super.validateProductPathName(path);
      if (err != null) {
         product = null;
         return err;
      }
      List<Product> products = Product.findByPathName(productPathName, store, 0, 10);
      if (products == null || products.size() == 0) {
         product = null;
         return "No product found with path name: " + productPathName;
      }
      else
         product = products.get(0);
      return null;
   }
}
