@CompilerSettings(compiledOnly=true)
currentUserbase {
   void init() {
      userbase = Userbase.findByAppName(defaultAppName);
      if (userbase == null) {
         System.out.println("*** Created default Userbase");
         userbase = new Userbase();
         userbase.appName = defaultAppName;
         userbase.dbInsert(false);
      }
   }
}
