@Sync
abstract class ParentDef extends ViewDef {
   @Sync(initDefault=true)
   List<ViewDef> childViewDefs;

   @sc.obj.ManualGetSet
   boolean equals(Object other) {
      if (other.getClass() != getClass())
         return false;

      ParentDef otherParent = (ParentDef) other;
      return DynUtil.equalObjects(childViewDefs, otherParent.childViewDefs);
   }

   @sc.obj.ManualGetSet
   int hashCode() {
      return childViewDefs == null || childViewDefs.size() == 0 ? 0 : childViewDefs.get(0).hashCode();
   }

   void dispose() {
      if (childViewDefs != null) {
         for (ViewDef childDef:childViewDefs)
            childDef.dispose();
      }
      super.dispose();
   }
}
