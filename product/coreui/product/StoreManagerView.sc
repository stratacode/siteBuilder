@Component @Sync
class StoreManagerView {
   String pathName;
   List<Storefront> storeList;
   Storefront store;

   Storefront lastStore;

   List<String> storeSelectList;

   String errorMessage;

   String storeError;
   String storeStatus;

   boolean showCreateView;
   boolean validStore;

   int storeIndex;

   boolean autoUpdatePath = false;
   boolean storeSaved = false;

   void updateStoreName(String val) {
      if (store == null)
         return;
      store.storeName = val;
      if (autoUpdatePath && (val != null && val.length() > 0))
         store.storePathName = CatalogElement.convertToPathName(val);
      if (storeSaved)
         store.validateProperties();
      else
         store.validateProp("storeName");
   }

   void updatePathName(String pathName) {
      if (store == null)
         return;
      autoUpdatePath = false;
      store.storePathName = pathName;
      store.validateProp("storePathName");
   }
}
