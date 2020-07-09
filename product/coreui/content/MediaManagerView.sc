MediaManagerView {
   String pathName;
   Storefront store;

   override @Exec(serverOnly=true)
   store := Storefront.findByStorePathName(pathName);

   mediaManager := store.mediaManager;
}
