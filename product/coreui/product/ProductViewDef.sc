@Sync(onDemand=true)
@CompilerSettings(compiledOnly=true)
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

   @sc.obj.ManualGetSet
   boolean equals(Object other) {
      return other instanceof ProductViewDef && DynUtil.equalObjects(productPathName, ((ProductViewDef) other).productPathName);
   }

   @sc.obj.ManualGetSet
   int hashCode() {
      return productPathName == null ? 0 : productPathName.hashCode();

   }
}
