class PageManager {
   @Sync(syncMode=SyncMode.Disabled)
   final static PageType plainPageType = new PageType(null, "Plain page", "sites", "sc.content.PageDef");

   @Sync(syncMode=SyncMode.Disabled)
   final static List<PageType> allPageTypes = new ArrayList<PageType>();

   @Sync(syncMode=SyncMode.Disabled)
   final static List<PageType> contentPageTypes = new ArrayList<PageType>();

   static {
      contentPageTypes.add(plainPageType);
      allPageTypes.addAll(contentPageTypes);
   }

   @Sync(syncMode=SyncMode.Disabled)
   final static ViewType contentViewType = new ViewType("Content view", "sc.content.ContentViewDef",
                                                        "sc.content.ContentViewEditor", "sc.content.ContentView");

   @Sync(syncMode=SyncMode.Disabled)
   final static ViewType slideShowViewType = new ViewType("Slideshow view", "sc.content.SlideshowDef",
                                                          "sc.content.SlideshowViewEditor", "sc.content.SlideshowView");

   static {
      plainPageType.viewTypes = new ArrayList<ViewType>();
      plainPageType.viewTypes.add(contentViewType);
      plainPageType.viewTypes.add(slideShowViewType);
   }

   static PageType findPageType(String pageTypePathName) {
      for (PageType pt:allPageTypes) {
         if (pt.pageTypePathName.equals(pageTypePathName)) {
            return pt;
         }
      }
      return null;
   }
}
