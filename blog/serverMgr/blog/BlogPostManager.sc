import java.util.Arrays;
import java.util.TreeSet;

import sc.lang.html.HTMLElement;

BlogPostManager {
   postContentHtml =: updatePostContent(postContentHtml);
   postPathName =: validatePostPathName();

   List<BlogPost> searchForText(String text) {
      return (List<BlogPost>) BlogPost.getDBTypeDescriptor().searchQuery(text, searchSite, getSearchSiteValues(), null, searchOrderBy, -1, -1);
   }

   void doSearch() {
      String txt = searchText == null ? "" : searchText;
      postList = searchForText(txt);
      if (postList.size() == 0) {
         int numPosts = BlogPost.getDBTypeDescriptor().searchCountQuery("", searchSite, getSearchSiteValues());
         if (numPosts == 0)
            searchStatusMessage = "No posts in the site";
         else
            searchStatusMessage = "No posts found out of: " + numPosts + " in the site";
      }
   }

   void doSearchAll() {
      searchText = "";
      doSearch();
   }

   void doSearchRecent() {
      searchText = "";
      List<BlogPost> recentPosts = (List<BlogPost>) BlogPost.getDBTypeDescriptor().query(Query.and(Query.eq("site", site), Query.gt("lastModified", new Date(System.currentTimeMillis() - recentMillis))), null, searchOrderBy, -1, -1);
      postList = recentPosts;
      if (postList.size() == 0) {
         int numPosts = BlogPost.getDBTypeDescriptor().searchCountQuery("", searchSite, getSearchSiteValues());
         if (numPosts == 0)
            searchStatusMessage = "No posts in the site";
         else
            searchStatusMessage = "No posts changed in last " + recentDays + " days out of: " + numPosts + " in store";
      }
   }

   void doSelectPost(BlogPost toSel) {
      clearFormErrors();
      // We might have just removed this post so don't make it current again
      if (toSel == null || ((DBObject)toSel.getDBObject()).isActive()) {
         if (toSel == post || toSel == null) {
            element = null;
            postSaved = false;
            parentCategoryPathName = "";
            postContentHtml = null;
         }
         else {
            element = toSel;
            postSaved = true;
            category = post.parentCategory;
            if (category != null && ((DBObject) category.getDBObject()).isActive()) {
               categoryEditable = true;
               categorySaved = true;
            }
            else
               categoryEditable = false;

            parentCategoryPathName = category == null ? "" : category.pathName;
            postContentHtml = post.postContent;
         }
      }
   }

   void validatePostPathName() {
      if (postPathName == null && post == null)
         return;
      if (postPathName != null && post != null && postPathName.equals(post.pathName))
         return;
      // Want to be sure the store path name has been set and the store updated if we are updating the skuCode and store name from the URL
         DynUtil.invokeLater(new Runnable() {
            void run() {
               if (postPathName == null && post != null)
                  doSelectPost(null);
               else if (postPathName != null && (post == null || !post.pathName.equals(postPathName)) && siteMgr.site != null) {
                  List<BlogPost> foundPosts = (List<BlogPost>) BlogPost.findByPathName(postPathName, siteMgr.site, 0, 1);
                  if (foundPosts != null && foundPosts.size() > 0)
                     doSelectPost(foundPosts.get(0));
               }
            }
         }, 0);
   }

   void removePost(long postId) {
      clearFormErrors();
      if (postId == 0) {
         errorMessage = "Invalid post id in remove";
         return;
      }
      BlogPost post = (BlogPost) BlogPost.getDBTypeDescriptor().findById(postId);
      if (post == null) {
         errorMessage = "Blog post not found to remove";
         System.err.println("*** removeBlogPost - blog post with id: " + postId + " not found");
         return;
      }
      errorMessage = null;
      try {
         int toRemIx = postList == null ? -1 : postList.indexOf(post);
         element = null;
         BlogCategory oldCat = post.parentCategory;
         if (oldCat != null) {
            post.parentCategory = null; // Will remove this from category.posts
            oldCat.validateAllPosts();
         }
         post.dbDelete(false);

         if (toRemIx != -1) {
            ArrayList<BlogPost> newList = new ArrayList<BlogPost>();
            for (int i = 0; i < postList.size(); i++) {
               if (i != toRemIx)
                  newList.add(postList.get(i));
            }
            postList = newList;
         }
      }
      catch (IllegalArgumentException exc) {
         errorMessage = "Failed to remove post: " + exc;
         return;
      }
   }

   void startAddPost(boolean doCopy) {
      if (addInProgress)
         return;

      clearFormErrors();
      postSaved = false;

      BlogPost newPost = (BlogPost) BlogPost.getDBTypeDescriptor().createInstance();
      if (post != null && doCopy) {
         newPost.name = "copy of " + post.name;
         newPost.pathName = post.pathName;
         newPost.mainMedia = post.mainMedia;
         newPost.name = post.name;
         newPost.postContent = post.postContent;
      }
      else {
         newPost.name = "";
         newPost.pathName = "";
         newPost.postContent = "";
      }
      element = newPost;

      autoUpdatePath = post.pathName == null || post.pathName.length() == 0;

      addInProgress = true;

      parentCategoryPathName = "";
   }

   void completeAddPost() {
      clearFormErrors();
      if (!post.validateProperties()) {
         errorMessage = post.formatErrors();
         postSaved = true;
         return;
      }

      if (site == null) {
         errorMessage = "No current site";
         return;
      }

      try {
         if (addInProgress) {
            post.site = site;
            post.dbInsert(false);

            String newName = post.pathName;

            // This after we have inserted it to prevent the category from trying to insert the post due to the
            // bi-directional parentCategory/posts relationships
            if (category != null)
               post.parentCategory = category;

            ArrayList<BlogPost> newList = new ArrayList<BlogPost>();
            newList.add(post);
            postList = newList;
            addInProgress = false;
            element = null;
            statusMessage = "Post " + newName + " created";
         }
         /*
         else {
            post.dbUpdate();
            statusMessage = "Changes saved";
         }
         */
      }
      catch (IllegalArgumentException exc) {
         errorMessage = "New post failed due to system error: " + exc;
      }
   }

   void cancelAddPost() {
      addInProgress = false;
      resetForm();
   }

   void doneEditingPost() {
      clearFormErrors();
      if (!post.validateProperties()) {
         errorMessage = post.formatErrors();
         postSaved = true;
         return;
      }
      element = null;
      postSaved = false;
      clearFormErrors();
   }

   void updateMatchingCategories(String pattern) {
      matchingCategories = getMatchingCategories(site, pattern);
   }

   void updateParentCategory(String pathName) {
      parentCategoryPathName = pathName;
      if (pathName == null || pathName.trim().length() == 0) {
         post.parentCategory = null;
         if (category != null)
            category.validateAllPosts();
         category = null;
         post.removePropError("parentCategory");
         return;
      }

      List<BlogCategory> cats = BlogCategory.findByPathName(pathName, site, 0, 10);
      boolean newPost = post.getDBObject().isTransient();
      if (cats != null && cats.size() > 0) {
         if (!newPost)
            post.parentCategory = cats.get(0);
         post.removePropError("parentCategory");
         // Not setting post.parentCategory here for new posts because it ends up inserting the post
         // because of the bi-directional relationship - the category is already added so once we set the post
         // to point to the category, the category wants to point to the post and adds it.
         category = cats.get(0);
         category.validateAllPosts();
      }
      else {
         post.addPropError("parentCategory", "No category with path name: " + pathName);
      }
   }

   // Options view

   void newCategoryCompleted(BlogCategory cat) {
      if (!addInProgress) {
         post.parentCategory = cat;
         if (cat != null)
            cat.validateAllPosts();
      }
      post.removePropError("parentCategory");
      parentCategoryPathName = cat.pathName;
   }

   void updatePostContent(String htmlText) {
      if (element == null || htmlText == null || DynUtil.equalObjects(post.postContent, htmlText))
         return;
      String error = HTMLElement.validateClientHTML(htmlText, HTMLElement.formattingTags, HTMLElement.formattingAtts);
      if (error == null)
         post.postContent = htmlText;
      else
         System.err.println("Invalid html text submission: " + htmlText + ": " + error);
   }
}
