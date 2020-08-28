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

   static List<PageType> contentPageTypes = new ArrayList<PageType>();
   static {
      contentPageTypes.add(plainPageType);
   }

   static ViewType contentViewType = new ViewType("Content view", "sc.content.ContentViewDef", "sc.content.ContentViewEditor");

   static {
      plainPageType.viewTypes = new ArrayList<ViewType>();
      plainPageType.viewTypes.add(contentViewType);
   }

   ViewType getViewTypeByClassName(String className) {
      for (ViewType vt:pageType.viewTypes)
         if (vt.viewClassName.equals(className))
            return vt;
      return null;
   }

   ViewType getViewTypeForViewDef(ViewDef viewDef) {
      String className = viewDef.getClass().getName();
      return getViewTypeByClassName(className);
   }

   PageType getPageTypeFromName(String pageTypeName) {
      for (PageType pt:pageTypes) {
         if (pt.pageTypeName.equals(pageTypeName)) {
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
