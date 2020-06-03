currentUserManager {
   void init() {
      mgr = UserManager.findByAppName(defaultAppName);
      if (mgr == null) {
         System.out.println("*** Created default UserManager");
         mgr = new UserManager();
         mgr.appName = defaultAppName;
         mgr.dbInsert(false);
      }
   }
}