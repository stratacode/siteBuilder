import java.util.Arrays;

BaseManager {
   Storefront store;

   @sc.obj.ManualGetSet
   void updateStoreRef(Storefront store) {
      this.store = store;
   }

   @sc.obj.ManualGetSet
   void updateSiteRef(SiteContext site) {
      this.site = site;
   }

   public void setStore(Storefront store) {
      this.store = store;
      updateSiteRef(store);
   }

   @sc.obj.ManualGetSet
   public void setSite(SiteContext site) {
      if (site instanceof Storefront)
         updateStoreRef(store);
      else
         updateStoreRef(null);
      this.site = site;
   }
}
