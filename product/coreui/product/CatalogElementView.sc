@Component
abstract class CatalogElementView {
   @Sync(syncMode=SyncMode.Disabled)
   String pathName;

   @Constant
   Storefront store = StoreView.store;

   @Sync
   List<Category> categoryPath;

   @Sync(initDefault=true)
   ManagedMedia currentMedia;

   @Sync
   List<ManagedMedia> altMedia;
   @Sync
   int altMediaIndex;

   altMediaIndex =: validateCurrentMedia();

   abstract CatalogElement getCatalogElement();

   void validateCatalogElement() {
      categoryPath = new ArrayList<Category>();
      CatalogElement elem = getCatalogElement();
      if (elem != null) {
         if (elem.parentCategory != null) {
            for (Category parent = elem.parentCategory; parent != null; parent = parent.parentCategory)
               categoryPath.add(0, parent);
         }
         if (elem instanceof Category)
            categoryPath.add((Category)elem);
      }
   }

   void validateCurrentMedia() {
      CatalogElement elem = getCatalogElement();
      if (elem == null) {
         currentMedia = null;
         altMedia = new ArrayList<ManagedMedia>();
         return;
      }
      ManagedMedia mainMedia = elem.mainMedia;
      if (elem.altMedia != null) {
         altMedia = new ArrayList<ManagedMedia>();
         if (!elem.altMedia.contains(mainMedia))
            altMedia.add(mainMedia);
         altMedia.addAll(elem.altMedia);
      }
      else {
         altMedia = new ArrayList<ManagedMedia>();
         if (mainMedia != null)
            altMedia.add(mainMedia);
      }
      currentMedia = altMediaIndex < altMedia.size() ? altMedia.get(altMediaIndex) : catalogElement.mainMedia;
   }

}
