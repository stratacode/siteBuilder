@sc.obj.EditorSettings(displayNameProperty="name")
abstract class CatalogElement extends ManagedResource {
   /* The external name */
   @Sync(resetState=true,initDefault=true)
   String name;

   @FindBy(paged=true,orderBy="-lastModified",with="store")
   @DBPropertySettings(indexed=true)
   @Sync(resetState=true,initDefault=true)
   String pathName;

   @Sync(resetState=true,initDefault=true)
   String shortDesc;

   @sc.obj.HTMLSettings(returnsHTML=true)
   // Don't allow the HTML Property to be set by the client to prevent code injection
   // TODO: we should have a filter attached to HTMLSettings to either make this the default or have a way
   // to add the validate method before setting the property.
   @Sync(syncMode=SyncMode.ServerToClient,initDefault=true)
   String longDesc;

   @Sync(resetState=true,initDefault=true)
   Category parentCategory;

   @DBPropertySettings(indexed=true)
   Storefront store;

   /** Used for the direct linking URL */
   String primaryKeywords;

   /** Used as page meta data for SEO */
   List<String> altKeywords;

   /** For the primary display for this element (images, video, etc) */
   @Sync(resetState=true,initDefault=true)
   ManagedMedia mainMedia;

   /** Primary display when navigating to this element */
   ManagedMedia navMedia;

   /** Alternative displays */
   @DBPropertySettings(columnType="jsonb")
   @Sync(resetState=true,initDefault=true)
   List<ManagedMedia> altMedia;

   static String validateName(String name) {
      return IPropValidator.validateRequired("name", name);
   }

   static String validatePathName(String displayPropName, String pn) {
      String res = IPropValidator.validateRequired(displayPropName, pn);
      if (res != null)
         return res;
      for (int i = 0; i < pn.length(); i++) {
         char c = pn.charAt(i);
         if (Character.isLetterOrDigit(c))
            continue;
         switch (c) {
            case '-':
            case '_':
            case '/':
               break;
            default:
               if (((int) c) > 127)
                  return "Illegal char in path name";
               return "Illegal character '" + c + "' in path name";
         }
      }
      return null;
   }

   static String convertToPathName(String val) {
      StringBuilder res = new StringBuilder();
      boolean newWord = false;
      for (int i = 0; i < val.length(); i++) {
         char c = val.charAt(i);
         if (Character.isLetterOrDigit(c)) {
            if (newWord) {
               res.append("-");
               newWord = false;
            }
         }
         else if (c != '-' && c != '_' && c != '/') {
            if (c == ' ')
               newWord = true;
            continue;
         }
         res.append(c);
      }
      return res.toString();
   }
}
