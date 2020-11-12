@Sync(onDemand=true)
@CompilerSettings(compiledOnly=true)
class BlogPostViewDef extends ViewDef {
   @Sync(initDefault=true)
   String postPathName;

   @Sync(initDefault=true)
   transient BlogPost post;

   String validatePostPathName(String path) {
      String err = ManagedResource.validatePathName("post path name", path);
      if (err != null)
         return err;
      return null;
   }
}
