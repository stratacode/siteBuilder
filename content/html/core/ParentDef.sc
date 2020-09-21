ParentDef {
   @Sync(syncMode=SyncMode.Disabled)
   transient List<IView> oldChildViews;
   @Sync(syncMode=SyncMode.Disabled)
   transient List<ViewDef> oldViewDefs;

   List<IView> createChildViews(PageView pageView) {
      if (childViews == null)
         return null;

      int sz = childViews.size();
      ArrayList<IView> res = new ArrayList<IView>(sz);

      boolean changed = false;
      for (int i = 0 ; i < sz; i++) {
         ViewDef viewDef = childViews.get(i);
         if (oldViewDefs != null && oldViewDefs.get(i) == viewDef)
            res.add(oldChildViews.get(i));
         else {
            res.add((IView) viewDef.createViewInstance(null, pageView, i));
            changed = true;
         }
      }
      if (changed) {
         oldViewDefs = new ArrayList<ViewDef>(childViews);
         return res;
      }
      return oldChildViews;
   }
}
