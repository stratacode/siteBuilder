PageManager {
   static {
      plainPageType.viewTypes.add(new ViewType("Blog post view", "sc.blog.BlogPostViewDef", "sc.blog.BlogPostViewEditor", "sc.blog.BlogPostView"));
      plainPageType.viewTypes.add(new ViewType("Blog category view", "sc.blog.BlogCategoryViewDef", "sc.blog.BlogCategoryViewEditor", "sc.blog.BlogCategoryView"));
   }
}
