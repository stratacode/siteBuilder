@Sync(onDemand=true)
class ViewType {
   @Sync(initDefault=true)
   String viewName;
   @Sync(initDefault=true)
   String viewDefClassName;
   @Sync(initDefault=true)
   String viewEditorClassName;
   @Sync(initDefault=true)
   String viewClassName;

   ViewType(String vn, String vdcn, String vecn, String vcn) {
      viewName = vn;
      viewDefClassName = vdcn;
      viewEditorClassName = vecn;
      viewClassName = vcn;
   }

   ViewType() {
   }

   String toString() {
      return viewName;
   }
}