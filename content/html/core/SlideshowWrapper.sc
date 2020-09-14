class SlideshowWrapper extends ChildViewWrapper {
   SlideshowView slideshow;

   Element createElement(Object viewDefObj, int ix, Element oldTag) {
      if (oldTag instanceof SlideshowElement) {
         SlideshowElement elem = (SlideshowElement) oldTag;
         if (elem.slideElement.repeatVar == viewDefObj)
            return oldTag;
      }
      Element viewElement = super.createElement(viewDefObj, ix, null);
      Element wrapper = new SlideshowElement(slideshow, allocUniqueId("slideshowElement"), viewElement, ix);
      return wrapper;
   }
}
