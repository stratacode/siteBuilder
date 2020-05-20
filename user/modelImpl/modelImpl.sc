public user.modelImpl extends model, jdbc.pgsql, jdbc.schemaManager {
   void init() {
      // Exclude the runtimes which do not support the DB features 
      excludeRuntimes("js", "android", "gwt");

      // The LayeredSystem is only available in the default Java runtime.
      addRuntime(null);
   }
}
