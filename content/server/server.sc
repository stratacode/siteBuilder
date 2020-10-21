package sc;

public content.server extends model, coreui {
   exportRuntime = false;
   compiledOnly = true;

   void init() {
      excludeRuntimes("js", "gwt");
   }
}
