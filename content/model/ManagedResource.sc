abstract class ManagedResource {
   MediaManager manager;

   Date lastModified;
   //UserProfile lastUpdatedBy;
   Date validStart;
   Date notValidAfter;

   //VersionHistory contentHistory;

   int serialCt; // Incremented on each content change

   //String contentVersionId; // Unique tag for the "version of this content" - including branch or deployment

   // int 'contentStatus' - ok, edited, conflict, rejected

   // int viewRolesMask, editRolesMask;  // bitmasks for quick matching against the user
   // List<Role> viewRoles, List<Role> editRoles;

   // Also add 'boolean viewAllowed(User)' hook for overriding basic logic

   public boolean isAvailable() {
      long now = System.currentTimeMillis();
      return (validStart == null || now > validStart.getTime()) && (notValidAfter == null || now < notValidAfter.getTime());
   }
}


