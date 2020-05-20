@Component scope<global>
object currentUserManager {
   UserManager mgr;

   static final String defaultAppName = "scDefaultApp";

   void init() {
      mgr = UserManager.findByAppName(defaultAppName);
      if (mgr == null) {
         System.out.println("*** Created default UserManager");
         mgr = new UserManager();
         mgr.appName = defaultAppName;
         mgr.dbInsert();
      }
   }
}
