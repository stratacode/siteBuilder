package sc.content;

public content.html.server extends content.coreui, content.server, user.server, servlet.schtml {
   // But don't export the server only constraint here
   exportRuntime = false;
   exportProcess = false;

   hidden = true;

   compiledOnly = true;
}
