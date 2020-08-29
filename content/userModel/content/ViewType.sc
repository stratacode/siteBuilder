class ViewType {
   String viewName;
   String viewDefClassName;
   String viewEditorClassName;
   String viewClassName;

   ViewType(String vn, String vdcn, String vecn, String vcn) {
      viewName = vn;
      viewDefClassName = vdcn;
      viewEditorClassName = vecn;
      viewClassName = vcn;
   }

   String toString() {
      return viewName;
   }
}