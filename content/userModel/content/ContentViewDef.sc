@CompilerSettings(compiledOnly=true)
class ContentViewDef extends ViewDef {
   String contentHtml = "";

   @sc.obj.ManualGetSet
   boolean equals(Object other) {
      return other instanceof ContentViewDef && DynUtil.equalObjects(contentHtml, ((ContentViewDef) other).contentHtml);
   }

   @sc.obj.ManualGetSet
   int hashCode() {
      return contentHtml == null ? 0 : contentHtml.hashCode();
   }
}
