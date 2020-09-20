@Sync(onDemand=true)
class ProductViewDef extends ViewDef {
   @Sync(initDefault=true)
   String productPathName;

   @Sync(initDefault=true)
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
