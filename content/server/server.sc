package sc;

public content.server extends model, userCoreui {
   exportRuntime = false;
   compiledOnly = true;

   void init() {
      excludeRuntimes("js", "gwt");
   }
}
