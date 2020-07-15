package sc.product;

// Extending user.modelImpl to get the server only dependency
public product.server extends content.server, product.coreui, user.server, user.modelImpl {
   // But don't export the server only constraint here
   exportRuntime = false;
   exportProcess = false;

   // Don't show in the management UI 
   hidden = true;
}
