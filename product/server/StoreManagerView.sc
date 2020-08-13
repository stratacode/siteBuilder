StoreManagerView {
   void start() {
      UserView uv = currentUserView;

      if (uv.loginStatus == LoginStatus.NotLoggedIn) {
         siteList = new ArrayList<SiteContext>();
         changeSite(null);
         errorMessage = "Please log in";
         return;
      }

      siteList = uv.user.siteList;
      //if (storeList == null) {
      //   storeList = new ArrayListSiteContext>();
      //   uv.user.storeList = storeList;
      //}

      Storefront newStore = null;
      SiteContext newSite = null;
      errorMessage = null;
      if (pathName != null) {
         newSite = SiteContext.findBySitePathName(pathName);
         if (newStore == null) {
            errorMessage = "No store with path name: " + pathName;
         }
      }
      else {
         if (siteList.size() == 0)
            errorMessage = "No current sites for user: " + uv.user.userName;
         else
            newSite = siteList.get(0);
      }
      if (newSite instanceof Storefront)
         newStore = (Storefront) newSite;
      else
         errorMessage = "Site with path name: " + pathName + " is not a store";

      changeSite(newStore);
      updateStoreSelectList();
   }

   void changeStoreWithIndex(int ix) {
      ix = ix - 1; // Skip the select title
      if (siteList == null || siteList.size() <= ix)
         errorMessage = "Invalid store selection";
      else
         changeSite(siteList.get(ix));
   }

   void changeSite(SiteContext newSite) {
      if (newSite != null) {
         if (!siteList.contains(newSite)) {
            errorMessage = "Not an admin of site: " + newSite.siteName;
            validStore = false;
         }
         else {
            if (newSite instanceof Storefront) {
               store = (Storefront) newSite;
               errorMessage = null;
               validStore = true;
            }
            else {
               store = null;
               errorMessage = "Site: " + newSite.siteName + " is not a store";
               validStore = false;
            }
         }
      }
      else {
         store = null;
         errorMessage = null;
         validStore = false;
      }
   }

   void startAddStore() {
      lastStore = store;
      store = (Storefront) Storefront.getDBTypeDescriptor().createInstance();
      showCreateView = true;
      validStore = false;
   }

   void completeAddStore() {
      storeError = storeStatus = null;

      if (currentUserView.loginStatus != LoginStatus.LoggedIn) {
         errorMessage = "Must be logged in to create a store";
         return;
      }

      if (store == null) {
         errorMessage = "No store";
         return;
      }

      store.validateProperties();

      if (store.propErrors == null) {
         UserProfile user = currentUserView.user;
         store.siteAdmins = new ArrayList<UserProfile>();
         store.siteAdmins.add(user);
         MediaManager newMgr = new MediaManager();
         newMgr.managerPathName = store.sitePathName;
         newMgr.mediaBaseUrl = "/images/" + newMgr.managerPathName + "/";
         newMgr.genBaseUrl = "/images/gen/" + newMgr.managerPathName + "/";
         store.mediaManager = newMgr;
         try {
            store.dbInsert(false);
         }
         catch (IllegalArgumentException exc) {
            storeError = "Error adding store: " + exc;
         }
         if (user.siteList == null) {
            user.siteList = new ArrayList<SiteContext>();
         }
         if (!user.siteList.contains(store)) {
            System.out.println("*** Reverse relationship did not add do the store list");
            user.siteList.add(store);
         }
         siteList = user.siteList;
         showCreateView = false;
         validStore = true;

         updateStoreSelectList();
      }
   }

   void cancelAddStore() {
      showCreateView = false;
      changeSite(lastStore);
      validStore = store != null;
      updateStoreSelectList();
   }

   void updateStoreSelectList() {
      if (!validStore)
         return; // Creating a new store

      ArrayList<String> res = new ArrayList<String>();
      if (siteList == null) {
         res.add("User: " + currentUserView.user.userName + " admin for no stores");
      }
      else {
         res.add("Select a store");
         for (SiteContext store:siteList)
            res.add(store.siteName);
      }
      storeSelectList = res;
      if (store == null)
         storeIndex = 0;
      else {
         int storeIx = siteList.indexOf(store);
         if (storeIndex == -1)
            System.err.println("*** Failed to find current store in siteList!");
         else
            storeIndex = storeIx + 1; // For the header message
      }
   }
}
