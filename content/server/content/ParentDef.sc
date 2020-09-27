ParentDef {
   void updateChildViews(Object parentNode, List<IView> childViews, PageView pageView, List<ViewDef> oldViewDefs) {
      if (childViewDefs == null)
         return;

      int sz = childViewDefs.size();
      ArrayList<IView> res = new ArrayList<IView>(sz);

      boolean changed = false;
      for (int i = 0 ; i < sz; i++) {
         ViewDef viewDef = childViewDefs.get(i);
         if (childViews.size() > i && oldViewDefs.get(i) == viewDef)
            res.add(childViews.get(i));
         else {
            res.add((IView) viewDef.createViewInstance(parentNode, pageView, i));
            changed = true;
         }
      }
      if (changed) {
         oldViewDefs.clear();
         oldViewDefs.addAll(childViewDefs);
         childViews.clear();
         childViews.addAll(res);
      }
      for (int i = 0; i < oldViewDefs.size(); i++) {
         ViewDef childViewDef = oldViewDefs.get(i);
         IView childView = childViews.get(i);
         if (childViewDef instanceof ParentDef && childView instanceof ParentView) {
            ParentView parentView = (ParentView) childView;
            ((ParentDef) childViewDef).updateChildViews(parentView, parentView.childViews, pageView, parentView.oldViewDefs);
         }
      }
   }
}
