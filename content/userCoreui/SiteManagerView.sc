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

   static String defaultSiteName = "Plain website";

   static List<String> newSiteTypes = new ArrayList<String>();
   static {
      newSiteTypes.add(defaultSiteName);
   }

   String newSiteName, newSitePathName, newSiteType = defaultSiteName;
   String newSiteError, newSiteStatus;

   void updateNewSiteName(String val) {
      newSiteName = val;
      newSiteError = newSiteStatus = null;
      if (autoUpdatePath && (val != null && val.length() > 0))
         newSitePathName = ManagedResource.convertToPathName(val);
      newSiteError = SiteContext.validateSiteName(val);
   }

   void updateNewPathName(String pathName) {
      autoUpdatePath = false;
      newSitePathName = pathName;
      newSiteError = SiteContext.validateSitePathName(pathName);
   }

   void updateNewSiteType(String newType) {
      newSiteType = newType;
   }
}
