BlogPostViewDef {
   Element createViewInstance(Object parentNode, PageView pageView, int ix) {
      BlogPostView postView = new BlogPostView(pageView, (Element)parentNode, parentNode == null ? null : ((Element)parentNode).allocUniqueId("postView"), this, ix);
      return postView;
   }
}
