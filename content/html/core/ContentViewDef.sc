ContentViewDef {
   Element createViewInstance(Element parentNode, PageView pageView, int ix) {
      return new ContentView(pageView, contentHtml, parentNode, parentNode == null ? null : parentNode.allocUniqueId("contentView"), this, ix);
   }
}