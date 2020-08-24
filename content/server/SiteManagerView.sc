SiteManagerView {
   void start() {
      UserView uv = currentUserView;

      if (uv.loginStatus == LoginStatus.NotLoggedIn) {
         siteList = new ArrayList<SiteContext>();
         changeSite(null);
         // Not clearing this error on login... it seems unnecessary anyway
         //errorMessage = "Please log in";
         return;
      }

      siteList = uv.user.siteList;
      //if (siteList == null) {
      //   siteList = new ArrayListSiteContext>();
      //   uv.user.siteList = siteList;
      //}

      SiteContext newSite = uv.lastSite;
      errorMessage = null;
      if (pathName != null) {
         newSite = SiteContext.findBySitePathName(pathName);
         if (newSite == null) {
            errorMessage = "No site with path name: " + pathName;
         }
      }
      else {
         if (siteList.size() == 0)
            errorMessage = "No current sites for user: " + uv.user.userName;
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
   }

   void startAddSite() {
      lastSite = site;
      site = (SiteContext) SiteContext.getDBTypeDescriptor().createInstance();
      showCreateView = true;
      validSite = false;
      addTypeName = "Site";
   }

   void completeAddSite() {
      siteError = siteStatus = null;

      if (currentUserView.loginStatus != LoginStatus.LoggedIn) {
         errorMessage = "Must be logged in to create a site";
         return;
      }

      if (site == null) {
         errorMessage = "No site";
         return;
      }

      site.validateProperties();

      if (site.propErrors == null) {
         UserProfile user = currentUserView.user;
         site.siteAdmins = new ArrayList<UserProfile>();
         site.siteAdmins.add(user);
         MediaManager newMgr = new MediaManager();
         newMgr.managerPathName = site.sitePathName;
         newMgr.mediaBaseUrl = "/images/" + newMgr.managerPathName + "/";
         newMgr.genBaseUrl = "/images/gen/" + newMgr.managerPathName + "/";
         site.mediaManager = newMgr;
         try {
            site.dbInsert(false);
         }
         catch (IllegalArgumentException exc) {
            siteError = "Error adding site: " + exc;
         }
         if (user.siteList == null) {
            user.siteList = new ArrayList<SiteContext>();
         }
         if (!user.siteList.contains(site)) {
            System.out.println("*** Reverse relationship did not add do the site list");
            user.siteList.add(site);
         }
         siteList = user.siteList;
         showCreateView = false;
         validSite = true;

         updateSiteSelectList();
      }
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
         res.add("User: " + currentUserView.user.userName + " admin for no sites");
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
}
