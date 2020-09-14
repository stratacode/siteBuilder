class PageManager extends BaseManager {
   String searchText = "";
   List<PageDef> currentPages;
   PageDef currentPage;

   boolean addInProgress = false;

   PageType pageType = plainPageType;

   String templateSearchText;
   PageDef newPageTemplate;

   ViewDef currentChildView;

   ParentDef currentParentDef;

   ViewType viewType = contentViewType;

   List<PageType> pageTypes = contentPageTypes;

   final static PageType plainPageType = new PageType(null, "Plain page", "sites", "sc.content.PageDef");

   final static List<PageType> allPageTypes = new ArrayList<PageType>();
   final static List<PageType> contentPageTypes = new ArrayList<PageType>();
   static {
      contentPageTypes.add(plainPageType);
      allPageTypes.addAll(contentPageTypes);
   }

   final static ViewType contentViewType = new ViewType("Content view", "sc.content.ContentViewDef", "sc.content.ContentViewEditor", "sc.content.ContentView");
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
