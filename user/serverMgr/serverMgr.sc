package sc.user;

public user.serverMgr extends user.server, content.serverMgr {
   exportRuntime = false;
   exportProcess = false;

   compiledOnly = true;

   void init() {
   // Exclude the runtimes which do not support the DB features
      excludeRuntimes("js", "android", "gwt");

      // The LayeredSystem is only available in the default Java runtime.
      addRuntime(null);
   }

}
