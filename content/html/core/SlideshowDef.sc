SlideshowDef {
   Element createViewInstance(Object parentNode, PageView pageView, int ix) {
      return new SlideshowView(pageView, (Element)parentNode, parentNode == null ? null : ((Element)parentNode).allocUniqueId("slideshowView"), this, ix);
   }
}
