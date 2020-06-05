@Component
abstract class CatalogElementView {
   @Sync(syncMode=SyncMode.Disabled)
   String pathName;

   @Constant
   Storefront store = StoreView.store;

   @Sync(initDefault=true)
   List<Category> categoryPath;

   //@Sync(initDefault=true)
   @Sync(syncMode=SyncMode.Disabled)
   ManagedMedia currentMedia;

   //@Sync
   @Sync(syncMode=SyncMode.Disabled)
   List<ManagedMedia> altMedia;

   @Sync
   int altMediaIndex;

   altMediaIndex =: validateCurrentMedia();

   abstract CatalogElement getCatalogElement();

   void validateCurrentMedia() {
      CatalogElement elem = getCatalogElement();
      if (elem == null) {
         if (currentMedia != null)
            currentMedia = null;
         if (altMedia == null || altMedia.size() > 0)
            altMedia = new ArrayList<ManagedMedia>();
         return;
      }
      ManagedMedia mainMedia = elem.mainMedia;
      List<ManagedMedia> newAltMedia;
      if (elem.altMedia != null) {
         newAltMedia = new ArrayList<ManagedMedia>();
         if (!elem.altMedia.contains(mainMedia))
            newAltMedia.add(mainMedia);
         newAltMedia.addAll(elem.altMedia);
      }
      else {
         newAltMedia = new ArrayList<ManagedMedia>();
         if (mainMedia != null)
            newAltMedia.add(mainMedia);
      }
      if (!DynUtil.equalObjects(altMedia, newAltMedia))
         altMedia = newAltMedia;
      ManagedMedia newCurrentMedia = altMediaIndex < altMedia.size() ? altMedia.get(altMediaIndex) : catalogElement.mainMedia;
      if (newCurrentMedia != currentMedia)
         currentMedia = newCurrentMedia;
   }

}
