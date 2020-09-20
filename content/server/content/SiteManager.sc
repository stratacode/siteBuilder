SiteManager {
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
      if (siteList == null || siteList.size() <= ix)
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
            validSite = true;
            site = newSite;

            validateSite();
         }
      }
      else {
         site = null;
         errorMessage = null;
         validSite = false;
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
         changeSite(newSite);
         if (!validSite)
            return;

         showCreateView = false;

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
      if (siteList == null) {
         res.add("User: " + userView.user.userName + " admin for no sites");
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
         int siteIx = siteList.indexOf(site);
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
      site.menuItems = site.menuItems;
   }

   void navMenuItemChanged() {
      saveMenuItems();
      if (currentMenuItem instanceof NavMenu)
         Bind.sendChangedEvent(currentMenuItem, "detailString");
   }

   void removeNavMenuItem(NavMenuDef navMenu, BaseMenuItem toRem) {
      navMenu.subMenuItems.remove(toRem);
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
}
