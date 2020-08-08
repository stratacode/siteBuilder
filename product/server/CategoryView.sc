CategoryView {
   // This binding is only on the server so that we don't run it more than once
   pathName =: validateCategoryPath();

   void init() {
      validateCategoryPath();
   }

   void validateCategoryPath() {
      String path = pathName;
      if (path == null)
         path = "/";
      if (category == null || !DynUtil.equalObjects(category.pathName, path)) {
         List<Category> cats = Category.findByPathName(path, storeView.store, 0, 1);
         if (cats.size() > 0) {
            category = cats.get(0);
            if (PTypeUtil.testMode)
               DBUtil.addTestIdInstance(category, "category/" + pathName);

            ManagedMedia mainMedia = category.mainMedia;
            if (category.altMedia != null) {
               altMedia = new ArrayList<ManagedMedia>();
               if (!category.altMedia.contains(mainMedia))
                  altMedia.add(mainMedia);
               altMedia.addAll(category.altMedia);
            }
            validateCurrentMedia();

            categoryViewError = null;
            startCategorySync();;

         }
         else {
            categoryViewError = "No category found for: " + (pathName == null ? "(root)" : pathName);
         }
      }

      validateCatalogElement();
   }

   void startCategorySync() {
      if (category != null) {
         SyncContext syncCtx = SyncManager.getSyncContextForInst(category);
         if (syncCtx == null) {
            SyncManager.addSyncInst(category, false, false, "appSession", null);
         }
         SyncManager.startSync(category, "products");
         SyncManager.startSync(category, "subCategories");
      }
   }
}