@Component @Sync
class StoreManagerView {
   String pathName;
   List<Storefront> storeList;
   Storefront store;

   String errorMessage;

   int getStoreIndex() {
      return storeList == null || store == null ? 0 : storeList.indexOf(store);
   }
}
