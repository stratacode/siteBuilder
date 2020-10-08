@Component
@CompilerSettings(constructorProperties="siteView,pathName")
@Sync
scope<appSession>
abstract class CatalogElementView implements IView {
   SiteView siteView;
   StoreView storeView := (StoreView) siteView;

   ViewDef viewDef;

   @Sync(syncMode=SyncMode.Disabled)
   String pathName;

   @Sync(initDefault=true)
   List<Category> categoryPath;

   //@Sync(initDefault=true)
   @Sync(syncMode=SyncMode.Disabled)
   ManagedMedia currentMedia;

   //@Sync
   @Sync(syncMode=SyncMode.Disabled)
   List<ManagedMedia> altMedia;

   @Sync(syncMode=SyncMode.Disabled)
   List<ManagedMedia> elementMedia;
   elementMedia =: mediaChanged();

   @Sync(syncMode=SyncMode.Disabled)
   ManagedMedia elementMainMedia;

   @Sync
   int elementMediaChangedCt;

   elementMainMedia =: mediaChanged();
   elementMediaChangedCt =: validateCurrentMedia();

   @Sync
   int altMediaIndex;

   altMediaIndex =: mediaChanged();

   int pageVisitCount := siteView.pageVisitCount;

   abstract CatalogElement getCatalogElement();

   void mediaChanged() {
      elementMediaChangedCt++;
   }

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
         if (mediaIsAvailable(mainMedia))
            newAltMedia.add(mainMedia);
         int numMedia = elem.altMedia.size();
         for (int i = 0; i < numMedia; i++) {
            ManagedMedia next = elem.altMedia.get(i);
            if (next != mainMedia && mediaIsAvailable(next))
               newAltMedia.add(next);
         }
      }
      else {
         newAltMedia = new ArrayList<ManagedMedia>();
         if (mainMedia != null)
            newAltMedia.add(mainMedia);
      }
      if (!DynUtil.equalObjects(altMedia, newAltMedia)) {
         altMedia = newAltMedia;
      }
      ManagedMedia newCurrentMedia = altMediaIndex < altMedia.size() ? altMedia.get(altMediaIndex) : catalogElement.mainMedia;
      if (newCurrentMedia != currentMedia)
         currentMedia = newCurrentMedia;
   }

   boolean mediaIsAvailable(ManagedMedia media) {
      return media.available;
   }

   void setViewDef(ViewDef viewDef) {
      this.viewDef = viewDef;
   }

   ViewDef getViewDef() {
      return viewDef;
   }

}
