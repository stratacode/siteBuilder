SiteManager {
   @Sync(initDefault=true)
   Storefront store;

   @Sync(initDefault=true)
   boolean validStore;

   static final String storeSiteName = "Online store";

   static {
      newSiteTypes.add(storeSiteName);
   }

   String getSiteTypeName(SiteContext ctx) {
      if (ctx instanceof Storefront)
         return storeSiteName;
      return super.getSiteTypeName(ctx);
   }
}
