@Sync
abstract class BaseProductManager extends BaseElementManager {
   @Sync(resetState=true, initDefault=true)
   String searchText;

   @Sync(resetState=true, initDefault=true)
   Category category;

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

   List<Category> matchingCategories;

   /**
    * Warning: stores the unvalidated html - needs to be sync'd for session reset to keep from losing the
    * local html changes.
    * When this property changes, a binding triggers updateLongDesc to do the validation and then update product.longDesc
    */
   // Not syncing this because the content widget property is already sync'd with reset state and syncing both causes
   // conflicts in the back and forth
   @Sync(resetState=true, initDefault=true)
   //@Sync(syncMode=SyncMode.Disabled)
   String longDescHtml;

   @Sync(resetState=true, initDefault=true)
   String parentCategoryPathName;

   void resetForm() {
      searchText = "";
      category = null;
      categorySaved = false;
      clearFormErrors();
   }

   void clearFormErrors() {
      uploadInProgress = false;
      super.clearFormErrors();

      categoryErrorMessage = categoryStatusMessage = null;
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

   CatalogElement getCatalogElement() {
      return (CatalogElement) element; // Product or Category
   }
}
