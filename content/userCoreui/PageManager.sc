class PageManager extends BaseManager {
   SiteContext site;

   String searchText;
   List<PageDef> currentPages;
   PageDef currentPage;

   boolean newPage = false; // Creating a new page or editing an existing one

   PageType newPageType = plainPageType;

   String templateSearchText;
   PageDef newPageTemplate;

   ViewDef currentChildView;

   ViewType newViewType = contentViewType;

   static PageType plainPageType = new PageType("Plain page", "sites", "sc.content.PageDef");

   static List<PageType> pageTypes = new ArrayList<PageType>();
   static {
      pageTypes.add(plainPageType);
   }

   static ViewType contentViewType = new ViewType("Content view", "sc.content.ContentViewDef", "sc.content.ContentViewEditor");

   static List<ViewType> viewTypes = new ArrayList<ViewType>();
   static {
      viewTypes.add(contentViewType);
   }

   static ViewType getViewTypeByClassName(String className) {
      for (ViewType vt:viewTypes)
         if (vt.viewClassName.equals(className))
            return vt;
      return null;
   }

   static ViewType getViewTypeForViewDef(ViewDef viewDef) {
      String className = viewDef.getClass().getName();
      return getViewTypeByClassName(className);
   }
}
