PageManager {
   static {
      pageTypes.add(new PageType("Store page", "stores", "sc.content.PageDef"));

      viewTypes.add(new ViewType("Product view", "sc.product.ProductViewDef", "sc.product.ProductViewEditor"));
      viewTypes.add(new ViewType("Category view", "sc.product.CategoryViewDef", "sc.product.CategoryViewEditor"));
   }
}