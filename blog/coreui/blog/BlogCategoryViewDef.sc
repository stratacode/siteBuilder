@Sync(onDemand=true)
@CompilerSettings(compiledOnly=true)
class BlogCategoryViewDef extends ViewDef {
   @Sync(initDefault=true)
   String categoryPathName;

   @Sync(initDefault=true)
   boolean showContentsOnly = false;

   transient BlogCategory category;

   String validateCategoryPathName(String path) {
      String err = ManagedResource.validatePathName("blog category path name", path);
      if (err != null)
         return err;
      return null;
   }
}
