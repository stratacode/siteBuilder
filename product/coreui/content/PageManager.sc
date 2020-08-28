PageManager {
   store := site instanceof Storefront ? (Storefront) site : null;

   static PageType storePageType = new PageType(plainPageType, "Store page", "stores", "sc.content.PageDef");
   static {
      pageTypes.add(storePageType);

      storePageType.viewTypes.add(new ViewType("Product view", "sc.product.ProductViewDef", "sc.product.ProductViewEditor"));
      storePageType.viewTypes.add(new ViewType("Category view", "sc.product.CategoryViewDef", "sc.product.CategoryViewEditor"));
   }
}