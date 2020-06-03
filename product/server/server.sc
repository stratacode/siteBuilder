package sc.product;

// Extending user.modelImpl to get the server only dependency
public product.server extends product.coreui, user.server, user.modelImpl {
   // But don't export the server only constraint here
   exportRuntime = false;
   exportProcess = false;
}
