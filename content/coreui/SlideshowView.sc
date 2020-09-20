@Sync(onDemand=true)
class SlideshowView implements IView {
   PageView pageView;

   SlideshowDef slideshowDef;

   int currentSlide;
   int numSlides := slideshowDef.childViews.size();

   ViewDef getViewDef() {
      return slideshowDef;
   }
   void setViewDef(ViewDef viewDef) {
      this.slideshowDef = (SlideshowDef) viewDef;
   }

   void changeSlide(int slideIx) {
      if (slideIx < 0)
         currentSlide = 0;
      else if (slideIx >= numSlides)
         currentSlide = numSlides - 1;
      else
         currentSlide = slideIx;
   }
}
