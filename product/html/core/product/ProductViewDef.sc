ProductViewDef {
   Element createViewInstance(Element parentNode, PageView pageView, int ix) {
      ProductView productView = new ProductView(pageView, parentNode, parentNode.allocUniqueId("productView"), this, ix);
      return productView;
   }
}
