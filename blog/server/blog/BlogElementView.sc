BlogElementView {
   void validateManagedElement() {
      super.validateManagedElement();

      ArrayList<BlogCategory> newCategoryPath = new ArrayList<BlogCategory>();
      BlogElement elem = getBlogElement();
      if (elem != null) {
         if (elem.parentCategory != null) {
            for (BlogCategory parent = elem.parentCategory; parent != null; parent = parent.parentCategory)
               newCategoryPath.add(0, parent);
         }
         //if (elem instanceof BlogCategory)
         //   newCategoryPath.add((BlogCategory)elem);
      }
      if (categoryPath == null || !DynUtil.equalObjects(categoryPath, newCategoryPath))
         categoryPath = newCategoryPath;
   }
}
