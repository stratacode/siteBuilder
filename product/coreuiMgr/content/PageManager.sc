PageManager {
   store := site instanceof Storefront ? (Storefront) site : null;

   void siteChanged() {
      super.siteChanged();
      if (site instanceof Storefront)
         pageTypes = storePageTypes;
      else
         pageTypes = contentPageTypes;
      addInProgress = false;
   }
}
