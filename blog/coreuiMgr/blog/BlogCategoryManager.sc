@Sync
class BlogCategoryManager extends BaseBlogManager {
   @Sync(initDefault=true)
   BlogCategory category;

   @Sync(initDefault=true)
   List<BlogCategory> categoryList;

   @Sync(initDefault=true)
   BlogCategory parentCategory;

   @Sync(resetState=true, initDefault=true)
   String findPostsText;

   @Sync(initDefault=true)
   List<BlogPost> matchingPosts;

   @Sync(resetState=true, initDefault=true)
   String categoryPathName;

   @Sync(initDefault=true)
   BlogPost selectedPost;
   boolean postAddValid = false;
   String addPostStatus;
   String addPostError;

   @Sync(initDefault=true)
   String postQueryText;
   @Sync(initDefault=true)
   List<String> postQuerySuggestions;
   String postQueryStatus;
   String postQueryError;

   void resetForm() {
      super.resetForm();
      categoryList = null;
      element = null;
      clearFormErrors();
      categoryPathName = null;
   }

   void clearFormErrors() {
      super.clearFormErrors();
   }

   String getElementType() {
      return "blog category";
   }

   void updateCategoryPathName(String pathName) {
      super.updateCategoryPathName(pathName);
      categoryPathName = pathName;
   }

   void updateCategoryName(String val) {
      super.updateCategoryName(val);
      if (category == null)
         categoryPathName = null;
      else
         categoryPathName = category.pathName;
   }

   void updateElementInstance(ManagedElement el) {
      category = (BlogCategory) el;
      if (el == null)
         categoryPathName = null;
      // Don't set this for unsaved products since it reflects in the location bar
      else if (category.id != 0 && category.pathName != null && category.pathName.length() > 0)
         categoryPathName = category.pathName;
   }
}
