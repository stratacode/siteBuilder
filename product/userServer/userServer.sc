package sc.user;

public product.userServer extends user.server, product.userModel, user.modelImpl, product.coreui, content.server {
   exportRuntime = false;
   exportProcess = false;

   void init() {
   // Exclude the runtimes which do not support the DB features
      excludeRuntimes("js", "android", "gwt");

      // The LayeredSystem is only available in the default Java runtime.
      addRuntime(null);
   }
}
