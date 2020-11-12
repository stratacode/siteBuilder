import java.util.Arrays;

PageManager {
   final static List<String> searchPropNames = Arrays.asList("site");

   boolean autoUpdatePath = true;

   pagePathName =: validatePagePathName();

   void doSearch() {
      List<PageDef> pageDefs = (List<PageDef>) PageDef.getDBTypeDescriptor().searchQuery(searchText, searchPropNames, getSearchSiteValues(), null, null, 0, 20);
      currentPages = pageDefs;
      currentPage = null;
      currentParentDef = null;
      pagePathName = null;
      if (pageDefs.size() == 0) {
         int numPages = PageDef.getDBTypeDescriptor().searchCountQuery("", searchPropNames, getSearchSiteValues());
         if (numPages == 0)
            searchStatusMessage = "There are no pages defined yet for this site";
         else
            searchStatusMessage = "No pages found out of: " + numPages + " in site";
      }
   }

   void updateNewPageType(PageType newPageType) {
      this.pageType = newPageType;
   }

   void createPageInstance() {
      Object pageClass = DynUtil.findType(pageType.pageDefClassName);
      if (pageClass == null)
         throw new IllegalArgumentException("No pageClassName: " + pageType.pageDefClassName);
      currentPage = (PageDef) DynUtil.newInnerInstance(pageClass, null, null);
      currentPage.pageTypePathName = pageType.pageTypePathName;
      currentPage.site = site;
      currentParentDef = currentPage;
      pagePathName = "";
   }

   void startCreatePage() {
      this.addInProgress = true;
      createPageInstance();
      autoUpdatePath = true;
   }

   void updatePageName(String name) {
      currentPage.pageName = name;
      if (currentPage.validateProp("pageName") && autoUpdatePath) {
         currentPage.pagePathName = ManagedResource.convertToPathName(name);
         pagePathName = currentPage.pagePathName;
      }
   }

   void updatePathName(String name) {
      currentPage.pagePathName = name;
      autoUpdatePath = false;
      currentPage.validateProp("pagePathName");
      pagePathName = name;
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
      if (currentParentDef == null) {
         System.err.println("*** No current parent for add child");
         return;
      }
      ViewDef newView = createViewDef(viewType);
      ViewDef curView = currentChildView;
      List<ViewDef> childViewDefs = currentParentDef.childViewDefs;
      boolean set = false;
      if (childViewDefs == null) {
         childViewDefs = new ArrayList<ViewDef>();
         set = true;
      }
      currentChildView = newView;
      if (curView == null) {
         if (append)
            childViewDefs.add(newView);
         else
            childViewDefs.add(0, newView);
      }
      else {
         int ix = childViewDefs.indexOf(curView);
         if (ix == -1 || (append && ix == childViewDefs.size()-1))
            childViewDefs.add(newView);
         else if (append)
            childViewDefs.add(ix+1, newView);
         else
            childViewDefs.add(ix, newView);
      }
      if (set)
         currentParentDef.childViewDefs = childViewDefs;
      updateChildViews();
   }

   void removePage(long id) {
      PageDef toRem = (PageDef) PageDef.getDBTypeDescriptor().findById(id);
      if (toRem == null)
         return;

      boolean isCurrent = toRem == currentPage;

      if (currentPages != null)
         currentPages.remove(toRem);

      if (isCurrent) {
         currentPage = null;
         currentParentDef = null;
         currentChildView = null;
         pagePathName = null;
      }

      toRem.dbDelete(false);
   }

   void updateChildViews() {
      if (currentPage != null && currentPage.childViewDefs != null) {
         // Updates the root property that stores the DB version by calling the setX method here (TODO: should have an api for this like 'DBObject.updateProperty()')
         currentPage.childViewDefs = currentPage.childViewDefs;
         // Need to mark the childViewDefs array list itself as changed to trigger bindings that listen for the value
         Bind.sendEvent(sc.bind.IListener.VALUE_CHANGED, currentPage.childViewDefs, null);
      }
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
      currentParentDef = null;
      currentChildView = null;
   }

   void cancelCreatePage() {
      currentPage = null;
      currentParentDef = null;
      currentChildView = null;
      addInProgress = false;
      pagePathName = null;
   }

   void removeViewDef(ViewDef viewDef, ParentDef parentDef) {
      if (!removeChildViewDef(parentDef, viewDef))
         System.err.println("*** Failed to find child view def to remove");
      else
         updateChildViews();
   }

   boolean removeChildViewDef(ParentDef parentDef, ViewDef viewDef) {
      if (parentDef.childViewDefs != null) {
         if (parentDef.childViewDefs.remove(viewDef))
            return true;
         /*
         for (ViewDef childView:parentDef.childViewDefs) {
            if (childView instanceof ParentDef) {
               if (removeChildViewDef((ParentDef) childView, viewDef))
                  return true;
            }
         }
         */
      }
      return false;
   }

   void selectViewDef(ViewDef viewDef, ParentDef parentDef) {
      if (viewDef == currentChildView) {
         currentChildView = null;
      }
      else {
         currentChildView = viewDef;
         currentParentDef = parentDef;
      }
   }

   void updateViewDef(ViewDef viewDef) {
      viewDef.site = site;
      viewDef.validateProperties();
      updateChildViews();
   }

   void updateCurrentPage(PageDef newPageDef) {
      currentPage = newPageDef;
      currentParentDef = newPageDef;
      if (newPageDef != null) {
         PageType newPageType = getPageTypeFromName(newPageDef.pageTypePathName);
         if (newPageType == null)
            System.err.println("*** Did not find pageType for current page");
         else if (newPageType != pageType) {
            pageType = newPageType;
            viewType = pageType.viewTypes.get(0);
         }
         pagePathName = newPageDef.pagePathName;
      }
      else {
         pageType = null;
         viewType = null;
         pagePathName = null;
      }
   }

   void selectParent(ParentDef newParent) {
      if (newParent != currentParentDef)
         currentParentDef = newParent;
   }

   void validatePagePathName() {
      if (pagePathName == null && currentPage == null)
         return;
      if (pagePathName != null && currentPage != null && pagePathName.equals(currentPage.pagePathName))
         return;
      // Want to be sure the store path name has been set and the store updated if we are updating the skuCode and store name from the URL
         DynUtil.invokeLater(new Runnable() {
            void run() {
               if (pagePathName == null && currentPage != null)
                  updateCurrentPage(null);
               else if (pagePathName != null && (currentPage == null || !DynUtil.equalObjects(currentPage.pagePathName, pagePathName)) && siteMgr != null && siteMgr.site != null) {
                  List<PageDef> foundPages = (List<PageDef>) PageDef.findByPagePathName(pagePathName, siteMgr.site);
                  if (foundPages != null && foundPages.size() > 0)
                     updateCurrentPage(foundPages.get(0));
               }
            }
         }, 0);
   }
}

