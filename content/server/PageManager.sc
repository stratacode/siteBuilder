import java.util.Arrays;

PageManager {
   static List<String> searchPropNames = Arrays.asList("site");

   boolean autoUpdatePath = true;

   void doSearch() {
      List<PageDef> pageDefs = (List<PageDef>) PageDef.getDBTypeDescriptor().searchQuery(searchText, searchPropNames, Arrays.asList(site), null, null, 0, 20);
      currentPages = pageDefs;
      currentPage = null;
   }

   void updateNewPageType(PageType newPageType) {
      this.pageType = newPageType;
   }

   void createPageInstance() {
      Object pageClass = DynUtil.findType(this.pageType.pageClassName);
      if (pageClass == null)
         throw new IllegalArgumentException("No pageClassName: " + this.pageType.pageClassName);
      currentPage = (PageDef) DynUtil.newInnerInstance(pageClass, null, null);
      currentPage.pageType = pageType.pageTypeName;
      currentPage.site = site;
   }

   void startCreatePage() {
      this.addInProgress = true;
      createPageInstance();
      autoUpdatePath = true;
   }

   void updatePageName(String name) {
      currentPage.pageName = name;
      if (currentPage.validateProp("pageName") && autoUpdatePath)
         currentPage.pagePathName = ManagedResource.convertToPathName(name);
   }

   void updatePathName(String name) {
      currentPage.pagePathName = name;
      autoUpdatePath = false;
      currentPage.validateProp("pagePathName");
   }

   ViewDef createViewDef(ViewType type) {
      String className = type.viewDefClassName;
      Object viewType = DynUtil.findType(className);
      if (viewType == null)
         throw new IllegalArgumentException("No view type found: " + className);
      Object res = DynUtil.newInnerInstance(viewType, null, null);
      if (!(res instanceof ViewDef))
         throw new IllegalArgumentException("View type: " + className + " must be subclass of sc.content.ViewDef");
      return (ViewDef) res;
   }

   void addNewView(boolean append) {
      if (currentPage == null)
         return;
      ViewDef newView = createViewDef(viewType);
      ViewDef curView = currentChildView;
      List<ViewDef> childViews = currentPage.childViews;
      boolean set = false;
      if (childViews == null) {
         childViews = new ArrayList<ViewDef>();
         set = true;
      }
      currentChildView = newView;
      if (curView == null) {
         if (append)
            childViews.add(newView);
         else
            childViews.add(0, newView);
      }
      else {
         int ix = childViews.indexOf(curView);
         if (ix == -1 || (append && ix == childViews.size()-1))
            childViews.add(newView);
         else if (append)
            childViews.add(ix+1, newView);
         else
            childViews.add(ix, newView);
      }
      if (set)
         currentPage.childViews = childViews;
   }

   void removePage(long id) {
      PageDef toRem = (PageDef) PageDef.getDBTypeDescriptor().findById(id);
      if (toRem == null)
         return;

      boolean isCurrent = toRem == currentPage;

      if (currentPages != null)
         currentPages.remove(toRem);

      if (isCurrent)
         currentPage = null;

      toRem.dbDelete(false);
   }

   void updateChildViews() {
      if (currentPage != null) // Update the DB copy by calling the setX method here
         currentPage.childViews = currentPage.childViews;
   }

   void savePageEdits() {
      if (currentPage == null)
         return;
      if (addInProgress && currentPage.getDBObject().isTransient()) {
         currentPage.dbInsert(true);
         addInProgress = false;
         if (currentPages == null)
            currentPages = new ArrayList<PageDef>();
         currentPages.add(0, currentPage);
      }
      currentPage = null;
   }

   void cancelCreatePage() {
      currentPage = null;
      addInProgress = false;
   }

   void removeViewDef(ViewDef viewDef) {
      if (currentPage == null || currentPage.childViews == null)
         return;
      if (!currentPage.childViews.remove(viewDef))
         System.err.println("*** Failed to find child view def to remove");
      else
         updateChildViews();
   }

   void updateViewDef(ViewDef viewDef) {
      viewDef.site = site;
      viewDef.validateProperties();
      updateChildViews();
   }

   void updateCurrentPage(PageDef newPageDef) {
      currentPage = newPageDef;
      if (newPageDef != null) {
         PageType newPageType = getPageTypeFromName(newPageDef.pageType);
         if (newPageType == null)
            System.err.println("*** Did not find pageType for current page");
         else if (newPageType != pageType) {
            pageType = newPageType;
            viewType = pageType.viewTypes.get(0);
         }
      }
      else {
         pageType = null;
         viewType = null;
      }
   }

}

