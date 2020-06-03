@DBTypeSettings(typeId=1)
class Category extends CatalogElement {
   /** Display this category in a navigation menu.  Some categories are hidden  */
   boolean navigateable;

   override @DBPropertySettings(reverseProperty="subCategories") parentCategory;

   @sc.obj.Sync(onDemand=true, initDefault=true)
   List<Product> products;

   @sc.obj.Sync(onDemand=true, initDefault=true)
   List<Category> subCategories;

   @sc.obj.Sync(onDemand=true)
   List<Category> relatedCategories;

   ManagedImage subCategoryImage;

   @sc.obj.Sync(onDemand=true)
   List<Category> categoryCrossSells;

   @sc.obj.Sync(onDemand=true)
   List<Category> categoryUpSells;

   override @FindBy(paged=true,orderBy="-lastModified",with="store") pathName;
}
