Userbase {
   static final String defaultAppName = "scDefaultApp";

   void initUserbase() {
      appName = defaultAppName;
      salt = DBUtil.createSalt();
   }
}