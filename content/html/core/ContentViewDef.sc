ContentViewDef {
   Element createViewInstance(Object parentNode, PageView pageView, int ix) {
      return new ContentView(pageView, contentHtml, (Element)parentNode, parentNode == null ? null : ((Element)parentNode).allocUniqueId("contentView"), this, ix);
   }
}