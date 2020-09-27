CategoryViewDef {
   Element createViewInstance(Object parentNode, PageView pageView, int ix) {
      CategoryView categoryView = new CategoryView(pageView, (Element) parentNode, parentNode == null ? null : ((Element)parentNode).allocUniqueId("categoryView"), this, ix);
      return categoryView;
   }
}
