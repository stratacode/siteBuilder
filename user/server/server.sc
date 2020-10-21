package sc.user;

public user.server extends content.coreui, user.modelImpl, user.postalCodeDB.server, content.server {
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
