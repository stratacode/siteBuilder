class PageType {
   PageType(String pageTypeName, String pageTypePathName, String className) {
      this.pageTypeName = pageTypeName;
      this.pageTypePathName = pageTypePathName;
      this.pageClassName = className;
   }

   String pageTypeName;
   String pageTypePathName;
   String pageClassName;

   public String toString() {
      return pageTypeName;
   }
}