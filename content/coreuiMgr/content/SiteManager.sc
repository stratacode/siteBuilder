@Component @Sync
class SiteManager {
   @Sync(resetState=true,initDefault=true)
   String pathName;

   @Sync(initDefault=true)
   List<SiteContext> siteList;
   @Sync(initDefault=true)
   SiteContext site;
   @Sync(initDefault=true)
   SiteContext lastSite;

   @Sync(syncMode=SyncMode.Disabled)
   UserView userView = currentUserView;

   @Sync(initDefault=true)
   List<String> siteSelectList;

   String errorMessage;

   String siteError;
   String siteStatus;

   @Sync(initDefault=true)
   boolean showCreateView;
   @Sync(initDefault=true)
   boolean validSite;

   @Sync(initDefault=true)
   boolean hasHomePage;

   @Sync(initDefault=true)
   int siteIndex;

   @Sync(initDefault=true)
   boolean autoUpdatePath = true;
   @Sync(initDefault=true)
   boolean siteSaved = false;

   static final String defaultSiteName = "Plain website";

   @Sync(syncMode=SyncMode.Disabled)
   static List<String> newSiteTypes = new ArrayList<String>();
   static {
      newSiteTypes.add(defaultSiteName);
   }

   String newSiteName, newSitePathName, newSiteType = defaultSiteName;
   String newSiteError, newSiteStatus;

   @Sync(initDefault=true)
   String searchText = "";
   @Sync(initDefault=true)
   List<SiteContext> currentSites;

   // Top-level selected navMenu or navMenuItem
   @Sync(initDefault=true)
   BaseMenuItem currentMenuItem;
   // Second-level menuItem
   @Sync(initDefault=true)
   NavMenuItem currentNavMenuItem;

   void doSearch() {
      BArrayList<SiteContext> matching = new BArrayList<SiteContext>();
      String lowerSearch = searchText.toLowerCase();
      for (SiteContext site:siteList) {
         if (site.sitePathName.toLowerCase().contains(lowerSearch) || site.siteName.toLowerCase().contains(lowerSearch))
            matching.add(site);
      }
      currentSites = matching;
   }

   void clearSearch() {
      searchText = "";
      currentSites = new BArrayList<SiteContext>(siteList);
   }

   void updateNewSiteName(String val) {
      newSiteName = val;
      newSiteError = newSiteStatus = null;
      if (autoUpdatePath && (val != null && val.length() > 0))
         newSitePathName = ManagedResource.convertToPathName(val);
      newSiteError = SiteContext.validateSiteName(val);
   }

   void validateNewPathName(String pathName) {
      autoUpdatePath = false;
      newSitePathName = pathName;
      newSiteError = SiteContext.validateSitePathName(pathName);
   }

   void updateNewSiteType(String newType) {
      newSiteType = newType;
   }

   void updateSiteName(String val) {
      site.siteName = val;
      site.validateProp("siteName");
   }

   void updatePathName(String pathName) {
      site.sitePathName = pathName;
      site.validateProp("sitePathName");
   }

   String getSiteTypeName(SiteContext ctx) {
      return SiteManager.defaultSiteName;
   }

   boolean isSiteAdmin(UserProfile profile) {
      if (profile == null)
         return false;
      return site.isSiteAdmin(profile);
   }

   void pageVisited() {
   }

   void updateIcon(String val) {
      site.icon = val;
      site.validateProp("icon");
   }

}
