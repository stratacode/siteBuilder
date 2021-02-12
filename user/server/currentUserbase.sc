@CompilerSettings(compiledOnly=true)
currentUserbase {
   void init() {
      try {
         userbase = Userbase.findByAppName(Userbase.defaultAppName);
         if (userbase == null) {
            System.out.println("*** Created default Userbase");
            userbase = new Userbase();
            userbase.initUserbase();
            userbase.dbInsert(false);
         }
         else if (userbase.salt == null) {
            System.out.println("*** Creating salt for Userbase: " + userbase.appName);
            userbase.salt = DBUtil.createSalt();
            userbase.dbUpdate();
         }
      }
      catch (IllegalArgumentException exc) {
         System.err.println("*** Error fetching default userbase: " + exc);
      }
   }
}
