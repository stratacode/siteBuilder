class ChildViewWrapper extends sc.lang.html.Div implements sc.lang.html.IRepeatWrapper {
   Element createElement(Object viewDefObj, int ix, Element oldTag) {
      Element elem = (Element) viewDefObj;
      elem.parentNode = this;
      elem.id = allocUniqueId("childView");
      return elem;

      /*
      ViewDef viewDef = (ViewDef) viewDefObj;
      ViewType viewType = pageView.pageType.getViewTypeForViewDef(viewDef);
      if (viewType == null)
         throw new IllegalArgumentException("Missing ViewType for view def");

      Object editorClass = DynUtil.findType(viewType.viewClassName);
      if (editorClass == null)
         throw new IllegalArgumentException("Missing view editor class");
      if (oldTag == null || editorClass != oldTag.getClass()) {
         Element newView = viewDef.createViewInstance(this, pageView, ix);
         return newView;
      }
      else {
         IView oldView = (IView) oldTag;
         if (viewDef != oldView.getViewDef())
            oldView.setViewDef(viewDef);
      }
      return oldTag;
      */
   }

   void repeatTagsChanged() {
   }


   void updateElementIndexes(int fromIx) {
   }
}
