StoreManagerView {
   void init() {
      store = Storefront.findByStorePathName(pathName);
      if (store == null) {
         errorMessage = "No store found with pathName";
      }
      else
         errorMessage = null;
      storeList = (List<Storefront>) Storefront.getDBTypeDescriptor().findAll(null, 0, 50);
   }

   void changeStore(Storefront store) {
      this.store = store;
   }
}
