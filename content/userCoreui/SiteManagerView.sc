@Component @Sync
class SiteManagerView {
   String pathName;
   List<SiteContext> siteList;
   SiteContext site;
   SiteContext lastSite;

   List<String> siteSelectList;

   String errorMessage;

   String siteError;
   String siteStatus;

   boolean showCreateView;
   boolean validSite;

   int siteIndex;

   boolean autoUpdatePath = true;
   boolean siteSaved = false;

   String addTypeName;

   void updateSiteName(String val) {
      if (site == null)
         return;
      site.siteName = val;
      if (autoUpdatePath && (val != null && val.length() > 0))
         site.sitePathName = ManagedResource.convertToPathName(val);
      if (siteSaved)
         site.validateProperties();
      else
         site.validateProp("siteName");
   }

   void updatePathName(String pathName) {
      if (site == null)
         return;
      autoUpdatePath = false;
      site.sitePathName = pathName;
      site.validateProp("sitePathName");
   }
}
