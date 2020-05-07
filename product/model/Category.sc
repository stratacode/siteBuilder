@DBTypeSettings(typeId=1)
class Category extends CatalogElement {
   /** Display this category in a navigation menu.  Some categories are hidden  */
   boolean navigateable;

   override @DBPropertySettings(reverseProperty="subCategories") parentCategory;

   List<Product> products;

   List<Category> subCategories;

   List<Category> relatedCategories;

   ManagedImage subCategoryImage;

   List<Category> categoryCrossSells;

   List<Category> categoryUpSells;
}
