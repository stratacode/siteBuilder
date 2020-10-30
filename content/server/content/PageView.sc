PageView {
   pagePathName =: validatePageView();
   childViewDefs =: validateChildViews();

   @Sync(syncMode=SyncMode.Disabled)
   transient String oldPagePathName;

   @Sync(syncMode=SyncMode.Disabled)
   transient PageDef oldPageDef;

   void init() {
      validatePageView();
   }

   void validatePageView() {
      // Need to avoid resetting the same pageDef to avoid recreating the tree while we are building it
      if (oldPageDef != null && pageDef == oldPageDef && DynUtil.equalObjects(oldPagePathName, pagePathName))
         return;

      List<PageDef> pageDefs;
      if (pagePathName == null)
         pageDefs = PageDef.findByHomePage(true, siteView.siteContext);
      else
         pageDefs = PageDef.findByPagePathName(pagePathName, siteView.siteContext);
      if (pageDefs == null || pageDefs.size() == 0) {
         SiteContext site = siteView.siteContext;
         if (site == null)
            errorMessage = "No site for path";
         else if (pagePathName == null)
            errorMessage = "No home page for site: " + siteView.siteContext.siteName;
         else
            errorMessage = "No page found with path name: " + pagePathName + " for site: " + siteView.siteContext.siteName;

         oldPageDef = null;
         oldPagePathName = null;
         return;
      }
      PageDef newPageDef = pageDefs.get(0);

      pageDef = pageDefs.get(0);
      pageType = PageManager.findPageType(pageDef.pageTypePathName);
      if (pageType == null)
         errorMessage = "No page type found: " + pageDef.pageType;

      oldPageDef = pageDef;
      oldPagePathName = pagePathName;

      validateChildViews();
   }

   void validateChildViews() {
      boolean setViews = false;
      List<IView> newChildViews = childViews;
      boolean newList = false;
      if (newChildViews == null) {
         newChildViews = new BArrayList<IView>();
         newList = true;
      }
      pageDef.updateChildViews(this, newChildViews, this, oldViewDefs);
      if (newList)
         childViews = newChildViews;
      oldViewDefs = new ArrayList<ViewDef>(pageDef.childViewDefs);
   }
}
