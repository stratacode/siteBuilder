class ProductViewDef extends ViewDef {
   String productPathName;

   transient Product product;

   Storefront getStore() {
      return (Storefront) site;
   }

   String validateProductPathName(String path) {
      String err = ManagedResource.validatePathName("product path name", path);
      if (err != null)
         return err;
      return null;
   }
}
