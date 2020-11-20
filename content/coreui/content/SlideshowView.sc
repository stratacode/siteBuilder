@Sync(onDemand=true)
class SlideshowView extends ParentView {
   @Sync(initDefault=true)
   PageView pageView;

   @Sync(initDefault=true)
   SlideshowDef slideshowDef;

   @Sync(initDefault=true)
   int currentSlide;
   int numSlides := slideshowDef.childViewDefs.size();

   List<ViewDef> childViewDefs := slideshowDef.childViewDefs;

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
