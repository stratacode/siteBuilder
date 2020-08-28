class PageManager extends BaseManager {
   SiteContext site;

   String searchText;
   List<PageDef> currentPages;
   PageDef currentPage;

   boolean addInProgress = false;

   PageType pageType = plainPageType;

   String templateSearchText;
   PageDef newPageTemplate;

   ViewDef currentChildView;

   ViewType viewType = contentViewType;

   static PageType plainPageType = new PageType(null, "Plain page", "sites", "sc.content.PageDef");

   static List<PageType> pageTypes = new ArrayList<PageType>();
   static {
      pageTypes.add(plainPageType);
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

   static PageType getPageTypeFromName(String pageTypeName) {
      for (PageType pt:pageTypes) {
         if (pt.pageTypeName.equals(pageTypeName)) {
            return pt;
         }
      }
      return null;
   }
}
