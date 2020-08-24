SiteManagerView {
   void start() {
      super.start();
      if (site instanceof Storefront) {
         store = (Storefront) site;
         validStore = true;
      }
      else
         validStore = false;
   }

   void changeSite(SiteContext newSite) {
      super.changeSite(newSite);
      if (newSite instanceof Storefront) {
         store = (Storefront) newSite;
         validStore = true;
      }
      else {
         store = null;
         validStore = false;
      }
   }

   void startAddSite() {
      super.startAddSite();
      validStore = false;
   }

   void startAddStore() {
      lastSite = site;
      store = (Storefront) Storefront.getDBTypeDescriptor().createInstance();
      site = store;
      showCreateView = true;
      validSite = false;
      validStore = false;
      addTypeName = "Store";
   }

   void completeAddSite() {
      super.completeAddSite();
      if (validSite && site instanceof Storefront) {
         store = (Storefront) site;
         validStore = true;
      }
      else {
         validStore = false;
         store = null;
      }
   }
}
