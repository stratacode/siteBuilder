@Sync
abstract class BaseBlogManager extends BlogElementManager {
   @Sync(resetState=true, initDefault=true)
   String searchText;

   @Sync(resetState=true, initDefault=true)
   BlogCategory category;

   @Sync(resetState=true, initDefault=true)
   boolean addCategoryInProgress;
   @Sync(resetState=true, initDefault=true)
   boolean showCategoryView;
   @Sync(resetState=true, initDefault=true)
   boolean categoryEditable;
   @Sync(resetState=true, initDefault=true)
   boolean categorySaved = false;

   boolean autoUpdateCategoryPath = false;

   String categoryErrorMessage;
   String categoryStatusMessage;
   String searchStatusMessage;

   List<BlogCategory> matchingCategories;

   @Sync(resetState=true, initDefault=true)
   String longDescHtml;

   @Sync(resetState=true, initDefault=true)
   String parentCategoryPathName;

   void resetForm() {
      searchText = "";
      category = null;
      categorySaved = false;
      searchStatusMessage = null;
      clearFormErrors();
   }

   void clearFormErrors() {
      uploadInProgress = false;
      super.clearFormErrors();

      categoryErrorMessage = categoryStatusMessage = null;
      searchStatusMessage = null;
   }

   void updateCategoryName(String val) {
      if (category == null)
         return;
      category.name = val;
      if (autoUpdateCategoryPath && (val != null && val.length() > 0))
         category.pathName = ManagedResource.convertToPathName(val);
      if (categorySaved)
         category.validateProperties();
      else
         category.validateProp("name");
   }

   void updateCategoryPathName(String pathName) {
      if (category == null)
         return;
      autoUpdateCategoryPath = false;
      category.pathName = pathName;
   }

}
