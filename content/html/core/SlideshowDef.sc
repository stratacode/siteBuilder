SlideshowDef {
   Element createViewInstance(Element parentNode, PageView pageView, int ix) {
      return new SlideshowView(pageView, parentNode, parentNode == null ? null : parentNode.allocUniqueId("slideshowView"), this, ix);
   }
}
