PageManager {
   store := site instanceof Storefront ? (Storefront) site : null;

   static List<PageType> storePageTypes = new ArrayList<PageType>(contentPageTypes);
   static PageType storePageType = new PageType(plainPageType, "Store page", "stores", "sc.content.PageDef");
   static {
      storePageTypes.add(storePageType);

      storePageType.viewTypes.add(new ViewType("Product view", "sc.product.ProductViewDef", "sc.product.ProductViewEditor"));
      storePageType.viewTypes.add(new ViewType("Category view", "sc.product.CategoryViewDef", "sc.product.CategoryViewEditor"));
   }

   void siteChanged() {
      super.siteChanged();
      if (site instanceof Storefront)
         pageTypes = storePageTypes;
      else
         pageTypes = contentPageTypes;
      addInProgress = false;
   }
}