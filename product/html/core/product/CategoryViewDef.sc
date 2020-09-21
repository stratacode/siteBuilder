CategoryViewDef {
   Element createViewInstance(Element parentNode, PageView pageView, int ix) {
      CategoryView categoryView = new CategoryView(pageView, parentNode, parentNode == null ? null : parentNode.allocUniqueId("categoryView"), this, ix);
      return categoryView;
   }
}
