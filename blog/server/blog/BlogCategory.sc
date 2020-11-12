import java.util.TreeSet;

BlogCategory {
   void validateAllPosts() {
      List<BlogPost> newList = null;
      TreeSet<String> postPaths = null;
      if (childPosts != null) {
         if (linkedPosts == null && postQuery == null) {
            allPosts = childPosts;
            return;
         }
         else {
            postPaths = new TreeSet<String>();
            newList = new BArrayList<BlogPost>();
            addAllPosts(newList, childPosts, postPaths);
            if (linkedPosts != null)
               addAllPosts(newList, linkedPosts, postPaths);
         }
      }
      else if (linkedPosts != null) {
         if (postQuery == null) {
            allPosts = linkedPosts;
            return;
         }
         else {
            newList = new BArrayList<BlogPost>();
            postPaths = new TreeSet<String>();

            addAllPosts(newList, linkedPosts, postPaths);
         }
      }

      if (postQuery != null) {
         List<BlogPost> dbList = (List<BlogPost>) BlogPost.getDBTypeDescriptor().query(postQuery, null, null, -1, -1);
         if (newList == null)
            newList = dbList;
         else {
            addAllPosts(newList, dbList, postPaths);
         }
      }
      allPosts = newList;
   }

   // Avoid adding two products with the same path name for the same category
   private void addAllPosts(List<BlogPost> newList, List<BlogPost> posts, TreeSet<String> postPaths) {
      for (BlogPost post:posts) {
         if (!postPaths.contains(post.pathName)) {
            postPaths.add(post.pathName);
            newList.add(post);
         }
      }
   }
}
