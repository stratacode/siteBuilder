@Sync(onDemand=true)
class PageType {
   PageType(PageType inheritFrom, String pageDisplayName, String pageTypePathName, String className) {
      this.pageDisplayName = pageDisplayName;
      this.pageTypePathName = pageTypePathName;
      this.pageDefClassName = className;
      this.viewTypes = new ArrayList<ViewType>();
      if (inheritFrom != null)
         this.viewTypes.addAll(inheritFrom.viewTypes);
   }

   PageType() {
   }

   @Sync(initDefault=true)
   String pageDisplayName; // e.g. Store page
   @Sync(initDefault=true)
   String pageTypePathName; // e.g. stores
   @Sync(initDefault=true)
   String pageDefClassName; // e.g. sc.content.PageDef - class name of the def object for this page type

   @Sync(initDefault=true)
   List<ViewType> viewTypes;

   public String toString() {
      return pageDisplayName;
   }

   ViewType getViewTypeByDefClassName(String className) {
      for (ViewType vt:viewTypes)
         if (vt.viewDefClassName.equals(className))
            return vt;
      return null;
   }

   ViewType getViewTypeForViewDef(ViewDef viewDef) {
      String className = viewDef.getClass().getName();
      return getViewTypeByDefClassName(className);
   }

}