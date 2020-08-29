class PageManager extends BaseManager {
   SiteContext site;

   site =: siteChanged();

   String searchText = "";
   List<PageDef> currentPages;
   PageDef currentPage;

   boolean addInProgress = false;

   PageType pageType = plainPageType;

   String templateSearchText;
   PageDef newPageTemplate;

   ViewDef currentChildView;

   ViewType viewType = contentViewType;

   List<PageType> pageTypes = contentPageTypes;

   static PageType plainPageType = new PageType(null, "Plain page", "sites", "sc.content.PageDef");

   static List<PageType> allPageTypes = new ArrayList<PageType>();
   static List<PageType> contentPageTypes = new ArrayList<PageType>();
   static {
      contentPageTypes.add(plainPageType);
      allPageTypes.addAll(contentPageTypes);
   }

   static ViewType contentViewType = new ViewType("Content view", "sc.content.ContentViewDef", "sc.content.ContentViewEditor", "sc.content.ContentView");

   static {
      plainPageType.viewTypes = new ArrayList<ViewType>();
      plainPageType.viewTypes.add(contentViewType);
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

   void siteChanged() {
      clearSearch();
   }

   void clearSearch() {
      searchText = "";
      currentPages = null;
      currentPage = null;
   }

}
