@Sync
class BlogPostManager extends BaseBlogManager {
   @Sync(resetState=true, initDefault=true)
   BlogPost post;

   @Sync(initDefault=true)
   List<BlogPost> postList;

   @Sync(resetState=true, initDefault=true)
   String postPathName;

   @Sync(resetState=true, initDefault=true)
   boolean addInProgress;

   boolean postSaved = false;

   /**
    * Save the un-validated html in a temporary property so that it gets sync'd properly for session reset
    * When this changes, a binding triggers updateLongDesc to do the validation and update product.longDesc
    */
   @Sync(resetState=true, initDefault=true)
   String postContentHtml;

   void updateElementInstance(ManagedElement el) {
      post = (BlogPost) el;
      if (el == null)
         postPathName = null;
      else if (post.id != 0 && post.pathName != null && post.pathName.length() > 0)
         postPathName = post.pathName;
   }

   void resetForm() {
      super.resetForm();
      postList = null;
      post = null;
      clearFormErrors();
      parentCategoryPathName = "";
      postPathName = null;
   }

   void clearFormErrors() {
      super.clearFormErrors();
      statusMessage = null;
      errorMessage = null;
   }

   String statusMessage;
   String errorMessage;

   @Sync(resetState=true, initDefault=true)
   boolean autoUpdatePath = false;

   void updatePostName(String val) {
      if (post == null)
         return;
      post.name = val;
      if (autoUpdatePath && (val != null && val.length() > 0))
         post.pathName = ManagedResource.convertToPathName(val);
      if (postSaved)
         post.validateProperties();
      else
         post.validateProp("name");
   }

   void updatePathName(String pathName) {
      if (post == null)
         return;
      autoUpdatePath = false;
      post.pathName = pathName;
      postPathName = pathName;
   }

   String getElementType() {
      return "blog post";
   }
}
