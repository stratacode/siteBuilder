@DBTypeSettings(typeId=1)
class Category extends CatalogElement {
   /** Display this category in a navigation menu.  Some categories are hidden  */
   boolean navigateable;

   override @DBPropertySettings(reverseProperty="subCategories") parentCategory;

   @sc.obj.Sync(onDemand=true, initDefault=true)
   List<Product> childProducts;

   @sc.obj.Sync(onDemand=true, initDefault=true)
   @DBPropertySettings(columnType="jsonb")
   List<Product> linkedProducts;

   @sc.obj.Sync(onDemand=true, initDefault=true)
   List<Category> subCategories;

   @sc.obj.Sync(onDemand=true, initDefault=true)
   @DBPropertySettings(columnType="jsonb")
   List<Category> linkedCategories;

   @sc.obj.Sync(onDemand=true)
   @DBPropertySettings(columnType="jsonb")
   List<Category> relatedCategories;

   @sc.obj.Sync(onDemand=true)
   @DBPropertySettings(columnType="jsonb")
   List<Category> categoryCrossSells;

   @sc.obj.Sync(onDemand=true)
   @DBPropertySettings(columnType="jsonb")
   List<Category> categoryUpSells;

   override @FindBy(paged=true,orderBy="-lastModified",with="store") pathName;

   @DBPropertySettings(columnType="jsonb")
   @Sync(syncMode=SyncMode.Disabled)
   Query productQuery;

   @DBPropertySettings(columnType="jsonb")
   @Sync(syncMode=SyncMode.Disabled)
   Query subCategoryQuery;

   @DBPropertySettings(persist=false)
   @sc.obj.Sync(initDefault=true)
   List<Product> allProducts;

   String pageUrl :=  "/" + store.sitePathTypeName + "/" + store.sitePathName + "/category/" + pathName;
}
