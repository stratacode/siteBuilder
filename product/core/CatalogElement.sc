class CatalogElement implements TrackableResource {
   String name;
   String shortDesc, longDesc;

   /** If false, this catalog element should be invisible and is not available for purchase */
   boolean liveProduct; 

   Category parentCategory;

   /** Used for the direct linking URL */
   String primaryKeywords;
   /** Used as page meta data for SEO */
   List<String> altKeywords;

   // Layer: multiCategory
   List<Category> parentCategories;

   // Layer: relatedProducts
   List<RelatedProduct> relatedCategories;

   // Layer for product media
   ProductImage productImage;
   ProductImage navImage;
   List<ProductImage> altViews;

   // TODO: encode in parameterized display template.  Using type groups, annotate a given template
   // from the page-type here, we can access the instance at runtime.  It will need to extend
   // the ProductTemplate class which has the annotation... that has a product property which
   // we populate in the dispatch of the template from the Display page.
}


