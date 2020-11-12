@sc.obj.EditorSettings(displayNameProperty="name")
@CompilerSettings(compiledOnly=true)
abstract class ManagedElement extends ManagedResource {
   /* The external name */
   @Sync(resetState=true,initDefault=true)
   String name;

   @DBPropertySettings(indexed=true)
   @Sync(resetState=true,initDefault=true)
   String pathName;

   String keywords;

   /** For the primary display for this element (images, video, etc) */
   @Sync(resetState=true,initDefault=true)
   ManagedMedia mainMedia;

   /** Primary display when navigating to this element */
   ManagedMedia navMedia;

   /** Alternative displays */
   @DBPropertySettings(columnType="jsonb")
   @Sync(resetState=true,initDefault=true)
   List<ManagedMedia> altMedia;

   @DBPropertySettings(persist=false)
   int mediaChangedCt;

   static String validateName(String name) {
      return IPropValidator.validateRequired("name", name);
   }

   static String validatePathName(String pathName) {
      return ManagedResource.validatePathName("path name", pathName);
   }
}
