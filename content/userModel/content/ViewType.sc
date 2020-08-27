class ViewType {
   String viewName;
   String viewClassName;
   String viewEditorClassName;

   ViewType(String vn, String vcn, String vecn) {
      viewName = vn;
      viewClassName = vcn;
      viewEditorClassName = vecn;
   }

   String toString() {
      return viewName;
   }
}