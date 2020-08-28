class ChildViewWrapper extends sc.lang.html.Div implements sc.lang.html.IRepeatWrapper {
   PageManager pageMgr;

   Element createElement(Object viewDefObj, int ix, Element oldTag) {
      ViewDef viewDef = (ViewDef) viewDefObj;
      ViewType viewType = pageMgr.getViewTypeForViewDef(viewDef);
      if (viewType == null)
         throw new IllegalArgumentException("Missing ViewType for view def");

      Object editorClass = DynUtil.findType(viewType.viewEditorClassName);
      if (editorClass == null)
         throw new IllegalArgumentException("Missing view editor class");
      if (oldTag == null || editorClass != oldTag.getClass()) {
         Element newEditor = (Element) DynUtil.newInnerInstance(editorClass, null,
                   "Lsc/lang/html/Element;Ljava/lang/String;Lsc/content/PageManager;Lsc/content/ViewDef;I",
                   this, this.allocUniqueId("childView"), pageMgr, viewDef, ix);
         return newEditor;
      }
      else {
         BaseViewEditor viewEditor = (BaseViewEditor) oldTag;
         if (viewDef != viewEditor.viewDef)
            viewEditor.viewDef = viewDef;
      }
      return oldTag;
   }

   void repeatTagsChanged() {
   }


   void updateElementIndexes(int fromIx) {
   }
}