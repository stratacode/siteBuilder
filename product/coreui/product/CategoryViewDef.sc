@Sync(onDemand=true)
@CompilerSettings(compiledOnly=true)
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

   boolean equals(Object other) {
      if (!(other instanceof CategoryViewDef))
         return false;
      CategoryViewDef otherCat = (CategoryViewDef) other;
      return DynUtil.equalObjects(categoryPathName, otherCat.categoryPathName) && showContentsOnly == otherCat.showContentsOnly;
   }

   int hashCode() {
      return categoryPathName == null ? 0 : categoryPathName.hashCode();
   }
}
