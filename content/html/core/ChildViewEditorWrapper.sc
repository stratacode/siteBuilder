/**
 * Used to create the proper view editor for the elements of the 'repeat' attribute for the
 * repeat tag used to render view editors as part of the PageManagerView
 */
class ChildViewEditorWrapper extends sc.lang.html.Div implements sc.lang.html.IRepeatWrapper {
   PageManager pageMgr;
   ParentDef parentDef;

   Element createElement(Object viewDefObj, int ix, Element oldTag) {
      ViewDef viewDef = (ViewDef) viewDefObj;
      ViewType viewType = pageMgr.pageType.getViewTypeForViewDef(viewDef);
      if (viewType == null)
         throw new IllegalArgumentException("Missing ViewType for view def");

      Object editorClass = DynUtil.findType(viewType.viewEditorClassName);
      if (editorClass == null)
         throw new IllegalArgumentException("Missing view editor class");
      if (oldTag == null || editorClass != oldTag.getClass()) {
         Element newEditor = (Element) DynUtil.newInnerInstance(editorClass, null,
                   "Lsc/lang/html/Element;Ljava/lang/String;Lsc/content/PageManager;Lsc/content/ViewDef;Lsc/content/ParentDef;I",
                   this, this.allocUniqueId("childView"), pageMgr, viewDef, parentDef, ix);
         return newEditor;
      }
      else {
         BaseViewEditor viewEditor = (BaseViewEditor) oldTag;
         if (viewDef != viewEditor.viewDef) {
            viewEditor.viewDef = viewDef;
            viewEditor.parentDef = parentDef;
         }
      }
      return oldTag;
   }

   void repeatTagsChanged() {
   }


   void updateElementIndexes(int fromIx) {
   }
}
