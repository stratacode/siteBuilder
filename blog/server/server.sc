package sc;

public blog.server extends content.server, blog.coreui, user.server, user.modelImpl {
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
