ProductViewDef {
   Element createViewInstance(Object parentNode, PageView pageView, int ix) {
      ProductView productView = new ProductView(pageView, (Element)parentNode, parentNode == null ? null : ((Element)parentNode).allocUniqueId("productView"), this, ix);
      return productView;
   }
}
