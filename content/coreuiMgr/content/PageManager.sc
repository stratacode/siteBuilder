@Sync
@sc.js.JSSettings(dependentTypes="sc.content.SlideshowViewEditor,sc.content.ContentViewEditor")
PageManager extends BaseManager {
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

   @Sync(initDefault=true)
   ViewType viewType = contentViewType;

   @Sync(initDefault=true)
   List<PageType> pageTypes = contentPageTypes;

   @Sync(resetState=true, initDefault=true)
   String pagePathName;

   String searchStatusMessage = "";

   PageType getPageTypeFromName(String pageTypePathName) {
      for (PageType pt:pageTypes) {
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
      pagePathName = null;
      searchStatusMessage = "";
   }
}
