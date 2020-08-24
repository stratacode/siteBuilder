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

   SiteContext createSite(String siteTypeName) {
      if (siteTypeName.equals(storeSiteName))
         return (Storefront) Storefront.getDBTypeDescriptor().createInstance();
      return super.createSite(siteTypeName);
   }
}
