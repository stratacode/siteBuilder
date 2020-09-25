@Sync(onDemand=true)
scope<appSession>
class SlideshowView implements IView {
   @Sync(initDefault=true)
   PageView pageView;

   @Sync(initDefault=true)
   SlideshowDef slideshowDef;

   @Sync(initDefault=true)
   int currentSlide;
   int numSlides := slideshowDef.childViewDefs.size();

   @Sync(initDefault=true)
   List<IView> childViews;

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
