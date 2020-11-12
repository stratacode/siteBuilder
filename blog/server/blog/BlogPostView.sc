BlogPostView {
   // Here so we only run it once and sync the results
   pathName =: validateBlogPath();

   pageVisitCount =: validateView();

   void init() {
      if (siteView.siteContext == null)
         return;
      validateBlogPath();
   }

   void validateView() {
   }

   void validateBlogPath() {
      if (pathName == null)
         return;

      if (post == null || !post.pathName.equals(pathName)) {
         List<BlogPost> posts = BlogPost.findByPathName(pathName, siteView.siteContext, 0, 1);
         if (posts.size() > 0) {
            post = posts.get(0);

            if (PTypeUtil.testMode)
               DBUtil.addTestIdInstance(post, "post/" + pathName);

            altMediaIndex = 0;
            mediaChanged();

            postViewError = null;

            /*
            UserSession us = siteView.getUserSession();
            if (us != null)
               us.addPostViewEvent(post);
            */
         }
         else {
            postViewError = "No post found for: " + pathName;
         }
      }

      validateManagedElement();
   }
}
