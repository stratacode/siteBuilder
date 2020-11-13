SiteManager {
   showCreateView =: updateCreateView();

   pathName =: validatePathName();

   void updateCreateView() {
      if (showCreateView) {
         validSite = false;
         lastSite = site;
      }
      else if (site != null)
         validSite = true;
   }

   void start() {
      if (userView.loginStatus == LoginStatus.NotLoggedIn) {
         siteList = new ArrayList<SiteContext>();
         changeSite(null);
         // Not clearing this error on login... it seems unnecessary anyway
         //errorMessage = "Please log in";
         return;
      }

      refreshSiteForUser();
   }

   void refreshSiteForUser() {
      siteList = userView.user.siteList;
      //if (siteList == null) {
      //   siteList = new ArrayListSiteContext>();
      //   uv.user.siteList = siteList;
      //}

      SiteContext newSite = userView.lastSite;
      errorMessage = null;
      if (pathName != null) {
         newSite = SiteContext.findBySitePathName(pathName);
         if (newSite == null) {
            errorMessage = "No site with path name: " + pathName;
         }
      }
      else {
         if (siteList == null || siteList.size() == 0)
            errorMessage = "No current sites for user: " + userView.user.userName;
         else if (newSite == null)
            newSite = siteList.get(0);
      }
      changeSite(newSite);
      updateSiteSelectList();
   }

   void changeSiteWithIndex(int ix) {
      ix = ix - 1; // Skip the select title
      if (siteList == null || siteList.size() <= ix || ix < 0)
         errorMessage = "Invalid site selection";
      else
         changeSite(siteList.get(ix));
   }

   void changeSite(SiteContext newSite) {
      if (newSite != null) {
         if (!siteList.contains(newSite)) {
            errorMessage = "Not an admin of site: " + newSite.siteName;
            validSite = false;
         }
         else {
            if (showCreateView)
               validSite = false;
            else
               validSite = true;
            site = newSite;
            pathName = newSite.sitePathName;

            validateSite();
         }
      }
      else {
         site = null;
         errorMessage = null;
         validSite = false;
         pathName = null;
      }
      UserView uv = currentUserView;
      if (uv != null)
         uv.lastSite = newSite;
      updateSiteSelectList();
   }

   void validateSite() {
      if (site != null) {
         List<PageDef> homePages = PageDef.findByHomePage(true, site);
         if (homePages == null || homePages.size() == 0)
            hasHomePage = false;
         else
            hasHomePage = true;
      }
   }

   void startAddSite() {
      lastSite = site;
      showCreateView = true;
      validSite = false;
   }

   void completeAddSite() {
      newSiteError = newSiteStatus = null;

      if (currentUserView.loginStatus != LoginStatus.LoggedIn) {
         newSiteError = "Must be logged in to create a site";
         return;
      }

      newSiteError = SiteContext.validateSiteName(newSiteName);
      if (newSiteError != null)
         return;
      newSiteError = SiteContext.validateSitePathName(newSitePathName);
      if (newSiteError != null)
         return;

      SiteContext newSite = createSite(newSiteType);
      if (newSite == null) {
         newSiteError = "No site";
         return;
      }

      newSite.siteName = newSiteName;
      newSite.sitePathName = newSitePathName;

      newSite.validateProperties();

      if (newSite.propErrors == null) {
         UserProfile user = userView.user;
         newSite.siteAdmins = new ArrayList<UserProfile>();
         newSite.siteAdmins.add(user);
         newSite.userbase = user.userbase;
         MediaManager newMgr = new MediaManager();
         newMgr.managerPathName = newSite.sitePathName;
         newMgr.mediaBaseUrl = "/images/" + newMgr.managerPathName + "/";
         newMgr.genBaseUrl = "/images/gen/" + newMgr.managerPathName + "/";
         newSite.mediaManager = newMgr;
         try {
            newSite.dbInsert(false);
         }
         catch (IllegalArgumentException exc) {
            newSiteError = "Error adding site: " + exc;
            return;
         }
         if (user.siteList == null) {
            user.siteList = new ArrayList<SiteContext>();
         }
         if (!user.siteList.contains(newSite)) {
            System.out.println("*** Reverse relationship did not add do the site list");
            user.siteList.add(newSite);
         }
         siteList = user.siteList;
         showCreateView = false;

         changeSite(newSite);
         if (!validSite)
            return;

         updateSiteSelectList();

         newSiteName = "";
         newSitePathName = "";
      }
   }

   SiteContext createSite(String siteTypeName) {
      if (siteTypeName.equals(defaultSiteName))
         return (SiteContext) SiteContext.getDBTypeDescriptor().createInstance();
      newSiteError = "Invalid type for new site: " + siteTypeName;
      return null;
   }

   void cancelAddSite() {
      showCreateView = false;
      changeSite(lastSite);
      validSite = site != null;
      updateSiteSelectList();
   }

   void updateSiteSelectList() {
      ArrayList<String> res = new ArrayList<String>();
      if (siteList == null || siteList.size() == 0) {
         res.add("No sites");
      }
      else {
         res.add("Select a site");
         for (SiteContext sites:siteList)
            res.add(sites.siteName);
      }
      siteSelectList = res;
      if (site == null)
         siteIndex = 0;
      else {
         int siteIx = siteList == null ? -1 : siteList.indexOf(site);
         if (siteIndex == -1)
            System.err.println("*** Failed to find current site in siteList!");
         else
            siteIndex = siteIx + 1; // For the header message
      }
   }

   void removeSite(long siteId) {
      SiteContext toRemove = (SiteContext) SiteContext.getDBTypeDescriptor().findById(siteId);
      if (toRemove != null) {
         boolean changeCurrentSite = site == toRemove;
         try {
            if (currentSites != null)
               currentSites.remove(toRemove);
            userView.user.siteList.remove(toRemove);
            userView.user.dbUpdate();

            toRemove.dbDelete(false);
         }
         catch (IllegalArgumentException exc) {
            errorMessage = "Unable to remove site: " + exc.toString();
            return;
         }
         if (changeCurrentSite) {
            changeSite(null);
         }
         updateSiteSelectList();
      }
   }

   void saveMenuItems() {
      // Need to call the setX method here to trigger the DB to save the value because we changed properties inside
      // the object.
      List<BaseMenuItem> menuItems = site.menuItems;
      site.menuItems = menuItems;
      // Need to mark the menuItems list changed so that menus will refer
      Bind.sendEvent(sc.bind.IListener.VALUE_CHANGED, menuItems, null);
      if (menuItems != null) {
         for (BaseMenuItem menuItem:menuItems) {
            menuItem.markChanged();
         }
      }
   }

   void navMenuItemChanged() {
      saveMenuItems();
      if (currentMenuItem instanceof NavMenu)
         Bind.sendChangedEvent(currentMenuItem, "detailString");
   }

   void updateMenuItemOrder(BaseMenuItem menuItem, String numberStr) {
      try {
         double orderValue = Double.parseDouble(numberStr);
         menuItem.orderValue = orderValue;
         saveMenuItems();
      }
      catch (NumberFormatException exc) {
         System.err.println("*** Error updating menuItemOrder: " + exc);
      }
   }

   void removeNavMenuItem(NavMenuDef navMenu, BaseMenuItem toRem) {
      navMenu.subMenuItems.remove(toRem);
      saveMenuItems();
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
      saveMenuItems();
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
      saveMenuItems();
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
      saveMenuItems();
   }

   void removeMenuItem(BaseMenuItem toRem) {
      if (site.menuItems != null)
         site.menuItems.remove(toRem);
      if (toRem == currentNavMenuItem)
         currentNavMenuItem = null;
      saveMenuItems();
   }

   void updateSiteAdmin(UserProfile profile, boolean newVal) {
      if (newVal) {
         if (site.siteAdmins == null)
            site.siteAdmins = new BArrayList<UserProfile>();
         if (site.siteAdmins.contains(profile))
            return;
         site.siteAdmins.add(profile);
      }
      else {
         if (site.siteAdmins == null)
            return;
         site.siteAdmins.remove(profile);
      }
   }

   void login() {
      if (userView.login()) {
         refreshSiteForUser();
      }
   }

   void pageVisited() {
      super.pageVisited();
      if (site != null)
         validateSite();
   }

   void validatePathName() {
      if (pathName == null && site == null)
         return;

      if (pathName != null)
         refreshSiteForUser();
      //else TODO - do we need this?
      //   changeSite(null);
   }

   void updateVisible(boolean visible) {
      site.visible = visible;
   }

   void updateIcon(String icon) {
      site.icon = icon;
      site.validateProp("icon");
   }
}
