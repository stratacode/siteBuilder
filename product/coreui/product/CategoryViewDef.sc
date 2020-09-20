@Sync(onDemand=true)
class CategoryViewDef extends ViewDef {
   @Sync(initDefault=true)
   String categoryPathName;

   @Sync(initDefault=true)
   boolean showContentsOnly = false;

   transient Category category;

   Storefront getStore() {
      return (Storefront) site;
   }

   String validateCategoryPathName(String path) {
      String err = ManagedResource.validatePathName("category path name", path);
      if (err != null)
         return err;
      return null;
   }
}
