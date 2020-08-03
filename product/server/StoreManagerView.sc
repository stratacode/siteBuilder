StoreManagerView {
   void start() {
      UserView uv = currentUserView;

      if (uv.loginStatus == LoginStatus.NotLoggedIn) {
         storeList = new ArrayList<Storefront>();
         changeStore(null);
         errorMessage = "Please log in";
         return;
      }

      storeList = uv.user.storeList;
      //if (storeList == null) {
      //   storeList = new ArrayList<Storefront>();
      //   uv.user.storeList = storeList;
      //}

      Storefront newStore = null;
      errorMessage = null;
      if (pathName != null) {
         newStore = Storefront.findByStorePathName(pathName);
         if (newStore == null) {
            errorMessage = "No store with path name: " + pathName;
         }
      }
      else {
         if (storeList.size() == 0)
            errorMessage = "No current stores for user: " + uv.user.userName;
         else
            newStore = storeList.get(0);
      }

      changeStore(newStore);
      updateStoreSelectList();
   }

   void changeStoreWithIndex(int ix) {
      ix = ix - 1; // Skip the select title
      if (storeList == null || storeList.size() <= ix)
         errorMessage = "Invalid store selection";
      else
         changeStore(storeList.get(ix));
   }

   void changeStore(Storefront newStore) {
      if (newStore != null) {
         if (!storeList.contains(newStore)) {
            errorMessage = "Not an admin of store: " + newStore.storeName;
            validStore = false;
         }
         else {
            store = newStore;
            errorMessage = null;
            validStore = true;
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
         store.storeAdmins = new ArrayList<UserProfile>();
         store.storeAdmins.add(user);
         try {
            store.dbInsert(false);
         }
         catch (IllegalArgumentException exc) {
            storeError = "Error adding store: " + exc;
         }
         if (user.storeList == null) {
            user.storeList = new ArrayList<Storefront>();
         }
         if (!user.storeList.contains(store)) {
            System.out.println("*** Reverse relationship did not add do the store list");
            user.storeList.add(store);
         }
         storeList = user.storeList;
         showCreateView = false;
         validStore = true;

         updateStoreSelectList();
      }
   }

   void cancelAddStore() {
      showCreateView = false;
      changeStore(lastStore);
      validStore = store != null;
      updateStoreSelectList();
   }

   void updateStoreSelectList() {
      if (!validStore)
         return; // Creating a new store

      ArrayList<String> res = new ArrayList<String>();
      if (storeList == null) {
         res.add("User: " + currentUserView.user.userName + " admin for no stores");
      }
      else {
         res.add("Select a store");
         for (Storefront store:storeList)
            res.add(store.storeName);
      }
      storeSelectList = res;
      if (store == null)
         storeIndex = 0;
      else {
         int storeIx = storeList.indexOf(store);
         if (storeIndex == -1)
            System.err.println("*** Failed to find current store in storeList!");
         else
            storeIndex = storeIx + 1; // For the header message
      }
   }
}
