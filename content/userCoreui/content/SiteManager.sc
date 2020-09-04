@Component @Sync
class SiteManager {
   String pathName;
   List<SiteContext> siteList;
   SiteContext site;
   SiteContext lastSite;

   UserView userView;

   List<String> siteSelectList;

   String errorMessage;

   String siteError;
   String siteStatus;

   boolean showCreateView;
   boolean validSite;

   int siteIndex;

   boolean autoUpdatePath = true;
   boolean siteSaved = false;

   static final String defaultSiteName = "Plain website";

   @Sync(syncMode=SyncMode.Disabled)
   static List<String> newSiteTypes = new ArrayList<String>();
   static {
      newSiteTypes.add(defaultSiteName);
   }

   String newSiteName, newSitePathName, newSiteType = defaultSiteName;
   String newSiteError, newSiteStatus;

   String searchText = "";
   List<SiteContext> currentSites;

   // Top-level selected navMenu or navMenuItem
   BaseMenuItem currentMenuItem;
   // Second-level menuItem
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

   void updateNewPathName(String pathName) {
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

   void addMenuItem() {
      NavMenuItem newItem = new NavMenuItem();
      List<BaseMenuItem> menuItems = site.menuItems;
      boolean set = false;
      if (menuItems == null) {
         menuItems = new BArrayList<BaseMenuItem>();
         set = true;
      }
      menuItems.add(newItem);
      if (set)
         site.menuItems = menuItems;
      currentMenuItem = newItem;
   }

   void addNavMenu() {
      NavMenuDef newMenu = new NavMenuDef();
      List<BaseMenuItem> menuItems = site.menuItems;
      boolean set = false;
      if (menuItems == null) {
         menuItems = new BArrayList<BaseMenuItem>();
         set = true;
      }
      menuItems.add(newMenu);
      if (set)
         site.menuItems = menuItems;
      currentMenuItem = newMenu;
   }

   void removeMenuItem(BaseMenuItem toRem) {
      if (site.menuItems != null)
         site.menuItems.remove(toRem);
   }

   void addSubMenuItem(NavMenuDef menu) {
      List<BaseMenuItem> menuItems = menu.menuItems;
      boolean set = false;
      if (menuItems == null) {
         menuItems = new BArrayList<BaseMenuItem>();
         set = true;
      }
      NavMenuItem newItem = new NavMenuItem();
      menuItems.add(newItem);
      if (set) {
         menu.subMenuItems = menuItems;
      }
      currentNavMenuItem = newItem;
   }
}
