package sc;

public content.serverMgr extends content.coreuiMgr, content.server {
   exportRuntime = false;
   compiledOnly = true;

   void init() {
      excludeRuntimes("js", "gwt");
   }
}
