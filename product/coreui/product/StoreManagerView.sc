@Component @Sync
class StoreManagerView {
   String pathName;
   List<SiteContext> siteList;
   Storefront store;

   Storefront lastStore;

   List<String> storeSelectList;

   String errorMessage;

   String storeError;
   String storeStatus;

   boolean showCreateView;
   boolean validStore;

   int storeIndex;

   boolean autoUpdatePath = true;
   boolean storeSaved = false;

   void updateSiteName(String val) {
      if (store == null)
         return;
      store.siteName = val;
      if (autoUpdatePath && (val != null && val.length() > 0))
         store.sitePathName = CatalogElement.convertToPathName(val);
      if (storeSaved)
         store.validateProperties();
      else
         store.validateProp("siteName");
   }

   void updatePathName(String pathName) {
      if (store == null)
         return;
      autoUpdatePath = false;
      store.sitePathName = pathName;
      store.validateProp("sitePathName");
   }
}
