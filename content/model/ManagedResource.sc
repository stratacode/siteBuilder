@CompilerSettings(compiledOnly=true)
abstract class ManagedResource implements IPropValidator {
   MediaManager manager;

   Date lastModified;
   //UserProfile lastUpdatedBy;
   Date validStart;
   Date notValidAfter;

   boolean visible = true;

   String revision;

   //VersionHistory contentHistory;

   int serialCt; // Incremented on each content change

   //String contentVersionId; // Unique tag for the "version of this content" - including branch or deployment or maybe 'revision'

   // int 'contentStatus' - ok, edited, conflict, rejected

   // int viewRolesMask, editRolesMask;  // bitmasks for quick matching against the user
   // List<Role> viewRoles, List<Role> editRoles;

   // Also add 'boolean viewAllowed(User)' hook for overriding basic logic

   public boolean isAvailable() {
      if (!visible)
         return false;
      long now = System.currentTimeMillis();
      return (validStart == null || now > validStart.getTime()) && (notValidAfter == null || now < notValidAfter.getTime());
   }

   @Bindable
   @DBPropertySettings(persist=false)
   Map<String,String> propErrors = null;

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
         else if (c != '-' && c != '_') {
            if (c == ' ')
               newWord = true;
            continue;
         }
         res.append(c);
      }
      return res.toString();
   }

   static String validateURL(String url, String pn, boolean required) {
      if (required) {
         String res = IPropValidator.validateRequired(url, pn);
         if (res != null)
            return res;
      }
      if (url.length() != 0 && !url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("/"))
         return "Invalid URL - must start with / or http:// or https://";
      for (int i = 0; i < url.length(); i++) {
         char c = url.charAt(i);
         if (c == ' ' || c == '\t' || ((int) c) > 127)
            return "Invalid URL at position: " + i;
      }
      return null;
   }
}


