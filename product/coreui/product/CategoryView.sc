class CategoryView extends CatalogElementView {
   Category category;

   pathName =: validateCategory();

   String categoryViewError;

   void init() {
      validateCategory();
   }

   CatalogElement getCatalogElement() {
      return category;
   }

   void validateCategory() {
      String path = pathName;
      if (path == null)
         path = "/";
      if (category == null || !DynUtil.equalObjects(category.pathName, path)) {
         List<Category> cats = Category.findByPathName(path, store, 0, 1);
         if (cats.size() > 0) {
            category = cats.get(0);
            ManagedMedia mainMedia = category.mainMedia;
            if (category.altMedia != null) {
               altMedia = new ArrayList<ManagedMedia>();
               if (!category.altMedia.contains(mainMedia))
                  altMedia.add(mainMedia);
               altMedia.addAll(category.altMedia);
            }
            validateCurrentMedia();

            categoryViewError = null;
         }
         else {
            categoryViewError = "No category found for: " + (pathName == null ? "(root)" : pathName);
         }
      }

      validateCatalogElement();
   }
}
