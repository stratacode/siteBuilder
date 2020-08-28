CategoryViewDef {
   String validateCategoryPathName(String path) {
      String err = super.validateCategoryPathName(path);
      if (err != null) {
         category = null;
         return err;
      }
      List<Category> categories = Category.findByPathName(categoryPathName, store, 0, 10);
      if (categories == null || categories.size() == 0) {
         category = null;
         return "No category found with path name: " + categoryPathName;
      }
      else
         category = categories.get(0);
      return null;
   }
}
