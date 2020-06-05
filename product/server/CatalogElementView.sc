CatalogElementView {
   void validateCatalogElement() {
      ArrayList<Category> newCategoryPath = new ArrayList<Category>();
      CatalogElement elem = getCatalogElement();
      if (elem != null) {
         if (elem.parentCategory != null) {
            for (Category parent = elem.parentCategory; parent != null; parent = parent.parentCategory)
               newCategoryPath.add(0, parent);
         }
         if (elem instanceof Category)
            newCategoryPath.add((Category)elem);
      }
      if (categoryPath == null || !DynUtil.equalObjects(categoryPath, newCategoryPath))
         categoryPath = newCategoryPath;
   }
}