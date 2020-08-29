class PageType {
   PageType(PageType inheritFrom, String pageTypeName, String pageTypePathName, String className) {
      this.pageTypeName = pageTypeName;
      this.pageTypePathName = pageTypePathName;
      this.pageClassName = className;
      this.viewTypes = new ArrayList<ViewType>();
      if (inheritFrom != null)
         this.viewTypes.addAll(inheritFrom.viewTypes);
   }

   String pageTypeName;
   String pageTypePathName;
   String pageClassName;

   List<ViewType> viewTypes;

   public String toString() {
      return pageTypeName;
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