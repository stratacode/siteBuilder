public user.coreuiImpl extends user.coreui, user.modelImpl {
   // The coreuiImpl layer won't be included in the js runtime due to inheriting
   // the constraint from jetty.schtml. Because this layer exposes remote
   // methods, we want layers to extend from it but not inherit the runtime
   // constraint.
   exportRuntime = false;
   exportProcess = false;
}
