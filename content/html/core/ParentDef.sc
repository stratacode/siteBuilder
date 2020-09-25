ParentDef {
   @Sync(syncMode=SyncMode.Disabled)
   transient List<IView> oldChildViews;
   @Sync(syncMode=SyncMode.Disabled)
   transient List<ViewDef> oldViewDefs;

   List<IView> createChildViews(PageView pageView) {
      if (childViewDefs == null)
         return null;

      int sz = childViewDefs.size();
      ArrayList<IView> res = new ArrayList<IView>(sz);

      boolean changed = false;
      for (int i = 0 ; i < sz; i++) {
         ViewDef viewDef = childViewDefs.get(i);
         if (oldViewDefs != null && oldViewDefs.get(i) == viewDef)
            res.add(oldChildViews.get(i));
         else {
            res.add((IView) viewDef.createViewInstance(null, pageView, i));
            changed = true;
         }
      }
      if (changed) {
         oldViewDefs = new ArrayList<ViewDef>(childViewDefs);
         return res;
      }
      return oldChildViews;
   }
}
