MediaManagerView {
   Storefront store;

   override @Exec(serverOnly=true)

   mediaManager := store.mediaManager;
}
