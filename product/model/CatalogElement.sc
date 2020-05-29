class CatalogElement extends ManagedResource {
   /* The external name */
   String name;

   @DBPropertySettings(indexed=true)
   String pathName;

   String shortDesc, longDesc;

   boolean visible; 

   Category parentCategory;

   @DBPropertySettings(indexed=true)
   Storefront store;

   /** Used for the direct linking URL */
   String primaryKeywords;

   /** Used as page meta data for SEO */
   List<String> altKeywords;

   /** For the primary display for this element (images, video, etc) */
   ManagedMedia mainMedia;

   /** Primary display when navigating to this element */
   ManagedMedia navMedia;

   /** Alternative displays */
   List<ManagedMedia> altMedia;
}
