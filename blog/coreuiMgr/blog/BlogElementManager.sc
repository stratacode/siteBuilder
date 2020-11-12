abstract class BlogElementManager extends BaseElementManager {
   @Sync(resetState=true, initDefault=true)
   String parentCategoryPathName;

   BlogElement getBlogElement() {
      return (BlogElement) element;
   }
}
