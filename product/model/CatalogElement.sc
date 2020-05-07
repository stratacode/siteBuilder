class CatalogElement extends ManagedResource {
   /* The external name */
   String name;
   /* The name to use in URLs to identify this element */
   String pathName;
   String shortDesc, longDesc;

   boolean visible; 

   Category parentCategory;

   /** Used for the direct linking URL */
   String primaryKeywords;

   /** Used as page meta data for SEO */
   List<String> altKeywords;

   ManagedImage productImage;
   ManagedImage navImage;
   List<ManagedMedia> altViews;
}
