@Sync
abstract class BaseProductManager extends BaseManager {
   private CatalogElement element;

   /**
    * The subclass has a product or category variable we use with bindings. Overriding the default get/set so that both that property and this
    * update to the new value before the events for either are sent.
    */
   @Sync(resetState=true, initDefault=true)
   @Bindable(manual=true)
   void setElement(CatalogElement el) {
      element = el;
      updateElementInstance(el);
      Bind.sendChange(this, "element", el);
      validateElement();
   }
   CatalogElement getElement() {
      return element;
   }

   void validateElement() {
   }

   abstract void updateElementInstance(CatalogElement el);

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

   String findMediaText;
   String mediaStatusMessage;
   String mediaErrorMessage;
   boolean uploadInProgress = false;

   List<Category> matchingCategories;

   List<ManagedMedia> matchingMedia;

   /**
    * Save the un-validated html in a temporary property so that it gets sync'd properly for session reset
    * When this changes, a binding triggers updateLongDesc to do the validation and update product.longDesc
    */
   @Sync(resetState=true, initDefault=true)
   String longDescHtml;

   @Sync(resetState=true, initDefault=true)
   String parentCategoryPathName;

   void clearMediaErrors() {
      mediaStatusMessage = null;
      mediaErrorMessage = null;
   }

   void resetForm() {
      searchText = "";
      category = null;
      categorySaved = false;
      clearFormErrors();
   }

   void clearFormErrors() {
      uploadInProgress = false;
      clearMediaErrors();

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

   abstract String getElementType();
}
