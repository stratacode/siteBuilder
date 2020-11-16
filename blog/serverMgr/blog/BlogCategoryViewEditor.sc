BlogCategoryViewEditor {
   void updateShowContentsOnly(boolean val) {
      categoryDef.showContentsOnly = val;
      pageMgr.updateViewDef(categoryDef);
   }
}
