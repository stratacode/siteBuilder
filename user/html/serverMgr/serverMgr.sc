package sc;

public user.html.serverMgr extends user.html.server, user.html.coreMgr, jetty.schtml {
   // But don't export the server only constraint here
   exportRuntime = false;
   exportProcess = false;

   compiledOnly = true;

   // Don't show in the management UI 
   hidden = true;

   void init() {
   // Split this layer and it's sublayers out into a new process using the default 'java' runtime
      includeProcess("server");
   }
}
