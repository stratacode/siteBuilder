package sc;

public blog.serverMgr extends blog.server, content.serverMgr, blog.coreuiMgr, user.serverMgr {
   // But don't export the server only constraint here
   exportRuntime = false;
   exportProcess = false;

   compiledOnly = true;

   // Don't show in the management UI 
   hidden = true;

   void init() {
   // Exclude the runtimes which do not support the DB features
      excludeRuntimes("js", "android", "gwt");

      // The LayeredSystem is only available in the default Java runtime.
      addRuntime(null);
   }
}
