SlideshowView {
   slideshowDef =: validateChildViews();
   childViewDefs =: validateChildViews();

   void validateChildViews() {
      boolean setViews = false;
      List<IView> newChildViews = childViews;
      boolean newList = false;
      if (newChildViews == null) {
         newChildViews = new BArrayList<IView>();
         newList = true;
      }
      slideshowDef.updateChildViews(this, newChildViews, pageView, oldViewDefs);
      if (newList)
         childViews = newChildViews;

      oldViewDefs = new ArrayList<ViewDef>(slideshowDef.childViewDefs);
   }
}