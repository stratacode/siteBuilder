package sc;

public user.html.serverMgr extends user.html.core, user.html.coreMgr, jetty.schtml {
   // But don't export the server only constraint here
   exportRuntime = false;
   exportProcess = false;

   compiledOnly = true;

   // Don't show in the management UI 
   hidden = true;

   void init() {
      // Split this layer and it's sublayers out into a new process using the default 'java' runtime
      addProcess(sc.layer.ProcessDefinition.create("Server", "java", true));
   }
}
