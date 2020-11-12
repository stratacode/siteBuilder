BlogCategoryViewDef {
   Element createViewInstance(Object parentNode, PageView pageView, int ix) {
      BlogCategoryView categoryView = new BlogCategoryView(pageView, (Element) parentNode, parentNode == null ? null : ((Element)parentNode).allocUniqueId("categoryView"), this, ix);
      return categoryView;
   }
}
