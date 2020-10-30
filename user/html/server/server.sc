package sc.user;

public user.html.server extends user.html.core, content.html.server, jetty.schtml {
   // This layer only runs on the server because it extends jetty.schtml
   // and no other processes. But since it exposes remote methods we want
   // accessible to other processes like the browser, we do not 
   // export the 'server only' constraint we inherited to layers that 
   // extend this layer.
   exportRuntime = false;
   exportProcess = false;
}
