@Sync
@sc.js.JSSettings(dependentTypes="sc.product.CategoryViewEditor,sc.product.ProductViewEditor,sc.content.SlideshowViewEditor,sc.content.ContentViewEditor")
class PageManager extends BaseManager {
   @Sync(resetState=true,initDefault=true)
   String searchText = "";
   @Sync(initDefault=true)
   List<PageDef> currentPages;
   @Sync(initDefault=true)
   PageDef currentPage;

   boolean addInProgress = false;

   PageType pageType = plainPageType;

   String templateSearchText;
   PageDef newPageTemplate;

   @Sync(initDefault=true)
   ViewDef currentChildView;

   @Sync(initDefault=true)
   ParentDef currentParentDef;

   @Sync(syncMode=SyncMode.Disabled)
   ViewType viewType = contentViewType;

   @Sync(syncMode=SyncMode.Disabled)
   List<PageType> pageTypes = contentPageTypes;

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
   final static ViewType contentViewType = new ViewType("Content view", "sc.content.ContentViewDef", "sc.content.ContentViewEditor", "sc.content.ContentView");
   @Sync(syncMode=SyncMode.Disabled)
   final static ViewType slideShowViewType = new ViewType("Slideshow view", "sc.content.SlideshowDef", "sc.content.SlideshowViewEditor", "sc.content.SlideshowView");

   static {
      plainPageType.viewTypes = new ArrayList<ViewType>();
      plainPageType.viewTypes.add(contentViewType);
      plainPageType.viewTypes.add(slideShowViewType);
   }

   PageType getPageTypeFromName(String pageTypePathName) {
      for (PageType pt:pageTypes) {
         if (pt.pageTypePathName.equals(pageTypePathName)) {
            return pt;
         }
      }
      return null;
   }

   static PageType findPageType(String pageTypePathName) {
      for (PageType pt:allPageTypes) {
         if (pt.pageTypePathName.equals(pageTypePathName)) {
            return pt;
         }
      }
      return null;
   }

   void clearSearch() {
      searchText = "";
      currentPages = null;
      currentPage = null;
   }
}
