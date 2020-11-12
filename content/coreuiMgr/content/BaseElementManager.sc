abstract class BaseElementManager extends BaseManager {
   ManagedElement element;

   /**
    * The subclass has a product or category variable we use with bindings. Overriding the default get/set so that both that property and this
    * update to the new value before the events for either are sent.
    */
   @Sync(resetState=true, initDefault=true)
   @Bindable(manual=true)
   void setElement(ManagedElement el) {
      element = el;
      updateElementInstance(el);
      Bind.sendChange(this, "element", el);
      validateElement();
   }
   ManagedElement getElement() {
      return element;
   }

   void validateElement() {
   }

   abstract void updateElementInstance(ManagedElement el);

   String findMediaText;
   String mediaStatusMessage;
   String mediaErrorMessage;
   boolean uploadInProgress = false;

   /**
    * Save the un-validated html in a temporary property so that it gets sync'd properly for session reset
    * When this changes, a binding triggers updateLongDesc to do the validation and update product.longDesc
    */
   @Sync(resetState=true, initDefault=true)
   String longDescHtml;

   void clearMediaErrors() {
      mediaStatusMessage = null;
      mediaErrorMessage = null;
   }

   abstract String getElementType();

   List<ManagedMedia> matchingMedia;

   void clearFormErrors() {
      clearMediaErrors();
   }
}
