import java.util.Arrays;
import java.util.TreeSet;
import java.util.HashSet;

import sc.lang.html.HTMLElement;
import sc.lang.sql.DBProvider;

import sc.parser.ParseError;
import sc.parser.ParseUtil;
import sc.lang.java.JavaSemanticNode;
import sc.lang.java.Expression;

BlogCategoryManager {
   categoryPathName =: validateCategoryPathName();

   List<BlogCategory> searchForText(String text) {
      return (List<BlogCategory>) BlogCategory.getDBTypeDescriptor().searchQuery(text, searchSite, getSearchSiteValues(), null, searchOrderBy, -1, -1);
   }

   void doSearch() {
      String txt = searchText == null ? "" : searchText;
      categoryList = searchForText(txt);
      if (categoryList.size() == 0) {
         int numCategories = BlogCategory.getDBTypeDescriptor().searchCountQuery("", searchSite, getSearchSiteValues());
         if (numCategories == 0)
            searchStatusMessage = "No blog categories created yet for this site";
         else
            searchStatusMessage = "No blog categories found out of: " + numCategories + " in site";
      }
   }

   void doSelectCategory(BlogCategory toSel) {
      clearFormErrors();
      // We might have just removed this post so don't make it current again
      if (toSel == null || ((DBObject)toSel.getDBObject()).isActive()) {
         if (toSel == category || toSel == null) {
            element = null;
            categorySaved = false;
            category = null;
            showCategoryView = false;
            categoryEditable = false;
            parentCategoryPathName = "";
            longDescHtml = null;
         }
         else {
            element = toSel;
            categorySaved = true;
            if (category != null && ((DBObject) category.getDBObject()).isActive()) {
               categoryEditable = true;
               categorySaved = true;
            }
            else
               categoryEditable = false;
            parentCategoryPathName = category.parentCategory != null ? category.parentCategory.pathName : "";
            category.validateAllPosts();
            longDescHtml = category.longDesc;
         }
      }
   }

   void removeCategory(long categoryId) {
      clearFormErrors();
      if (categoryId == 0) {
         categoryErrorMessage = "Invalid category id in remove";
         return;
      }
      BlogCategory cat = (BlogCategory) BlogCategory.getDBTypeDescriptor().findById(categoryId);
      if (cat == null) {
         categoryErrorMessage = "Category not found to remove";
         System.err.println("*** removeCategory - blog with id: " + categoryId + " not found");
         return;
      }
      List<Object> propValues = Arrays.asList(cat);
      List<BlogPost> postRefs = (List<BlogPost>) BlogPost.getDBTypeDescriptor().findBy(Arrays.asList("parentCategory"), propValues, null, null, 0, 1);
      if (postRefs != null && postRefs.size() > 0) {
         categoryErrorMessage = "Unable to remove category that still has posts: " + postRefs;
         return;
      }
      List<BlogCategory> catRefs = (List<BlogCategory>) BlogCategory.getDBTypeDescriptor().findBy(Arrays.asList("parentCategory"), propValues, null, null, 0, 1);
      if (catRefs != null && catRefs.size() > 0) {
         categoryErrorMessage = "Unable to remove category that still has categories: " + catRefs;
         return;
      }
      categoryErrorMessage = null;
      try {
         int toRemIx = categoryList == null ? -1 : categoryList.indexOf(cat);
         element = null;
         if (cat.parentCategory != null)
            cat.parentCategory = null; // Will remove this from category.subCategories
         cat.dbDelete(false);

         if (toRemIx != -1) {
            ArrayList<BlogCategory> newList = new ArrayList<BlogCategory>();
            for (int i = 0; i < categoryList.size(); i++) {
               if (i != toRemIx)
                  newList.add(categoryList.get(i));
            }
            categoryList = newList;
         }
      }
      catch (IllegalArgumentException exc) {
         categoryErrorMessage = "Failed to remove category: " + exc;
         return;
      }
   }

   void startAddCategory(boolean doCopy) {
      if (addCategoryInProgress)
         return;

      clearFormErrors();
      categorySaved = false;

      BlogCategory newCat = (BlogCategory) BlogCategory.getDBTypeDescriptor().createInstance();
      if (category != null && doCopy) {
         newCat.name = "copy of " + category.name;
         newCat.pathName = category.pathName;
         newCat.mainMedia = category.mainMedia;
         newCat.longDesc = category.longDesc;
         newCat.shortDesc = category.shortDesc;
         if (newCat.longDesc == null)
            newCat.longDesc = "";
         if (newCat.shortDesc == null)
            newCat.shortDesc = "";
      }
      else {
         newCat.name = "";
         newCat.pathName = "";
         newCat.longDesc = "";
         newCat.shortDesc = "";
      }
      element = newCat;

      // Automatically set this based on the post name unless it's already specified
      autoUpdateCategoryPath = category.pathName == null || category.pathName.length() == 0;

      addCategoryInProgress = true;
   }

   void startAddCategory() {
      if (addCategoryInProgress)
         return;
      initTemporaryCategory();
      addCategoryInProgress = true;
      showCategoryView = true;
      categoryStatusMessage = categoryErrorMessage = null;
      categoryEditable = false;
      autoUpdateCategoryPath = true;
   }

   void initTemporaryCategory() {
      category = (BlogCategory) BlogCategory.getDBTypeDescriptor().createInstance();
      category.site = site;
      category.name = "";
      category.pathName = "";
      parentCategoryPathName = "";
   }

   void cancelAddCategory() {
      addCategoryInProgress = false;
      category = null;
      categoryErrorMessage = null;
      categoryStatusMessage = "Add category cancelled";
      showCategoryView = false;
      parentCategoryPathName = "";
   }

   void doneEditingCategory() {
      clearFormErrors();
      if (!category.validateProperties()) {
         categoryErrorMessage = category.formatErrors();
         categorySaved = true;
         return;
      }
      element = null;
      categorySaved = false;
      showCategoryView = false;
      parentCategoryPathName = "";
   }

   void newCategoryCompleted(BlogCategory newCategory) {
      if (parentCategory != null)
         newCategory.parentCategory = parentCategory;
      ArrayList<BlogCategory> newList = new ArrayList<BlogCategory>();
      newList.add(newCategory);
      categoryList = newList;
      element = null;
      parentCategoryPathName = "";
   }


   void updateMatchingCategories(String pattern) {
      if (pattern == null || pattern.length() < 2)
         return;
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<BlogCategory> res = new ArrayList<BlogCategory>();
      List<BlogCategory> allMatches = (List<BlogCategory>) BlogCategory.getDBTypeDescriptor().searchQuery(pattern, null, null, null, searchOrderBy, 0, 20);
      for (BlogCategory match:allMatches) {
         if (!found.contains(match.pathName)) {
            res.add(match);
            found.add(match.pathName);
            if (res.size() == 10)
               break;
         }
      }
      matchingCategories = res;
   }

   String checkParentCategory(BlogCategory current, BlogCategory newParent, int ix) {
      if (newParent == category) {
         if (ix == 0)
            return "Parent should be a different category";
         else
            return "New parent category is a child of the old one";
      }
      BlogCategory nextParent = newParent.parentCategory;
      if (nextParent != null)
         return checkParentCategory(current, nextParent, ++ix);
      return null;
   }

   void updateParentCategory(String pathName) {
      parentCategoryPathName = pathName;
      if (pathName == null || pathName.trim().length() == 0) {
         category.parentCategory = null;
         category.removePropError("parentCategory");
         return;
      }

      List<BlogCategory> cats = BlogCategory.findByPathName(pathName, site, 0, 10);
      boolean newCategory = category.getDBObject().isTransient();
      if (cats != null && cats.size() > 0) {
         parentCategory = cats.get(0);
         String error = checkParentCategory(category, parentCategory, 0);
         if (error != null) {
            parentCategory = null;
            category.addPropError("parentCategory",  error);
            return;
         }

         if (!newCategory)
            category.parentCategory = parentCategory;
         category.removePropError("parentCategory");
         // Not setting post.parentCategory here for new posts because it ends up inserting the post
         // because of the bi-directional relationship - the category is already added so once we set the post
         // to point to the category, the category wants to point to the post and adds it.
      }
      else
         category.addPropError("parentCategory", "No category with path name: " + pathName);
   }

   void updateMatchingPosts(String pattern) {
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<BlogPost> res = new ArrayList<BlogPost>();
      List<BlogPost> allMatches = (List<BlogPost>) BlogPost.getDBTypeDescriptor().searchQuery(pattern, null, null, null, searchOrderBy, 0, 20);
      for (BlogPost match:allMatches) {
         if (!found.contains(match.pathName)) {
            res.add(match);
            found.add(match.pathName);
            if (res.size() == 10)
               break;
         }
      }
      matchingPosts = res;
   }

   void updateSelectedPost(String pathName) {
      addPostError = null;
      addPostStatus = null;
      List<BlogPost> posts = BlogPost.findByPathName(pathName, site, 0, 1);
      if (posts == null || posts.size() == 0) {
         selectedPost = null;
         postAddValid = false;
      }
      else {
         selectedPost = posts.get(0);
         if (category.allPosts != null && category.allPosts.contains(selectedPost)) {
            postAddValid = false;
            if (selectedPost.parentCategory == category)
               addPostError = "Post already a child of this category";
            else if (category.linkedPosts != null && category.linkedPosts.contains(selectedPost))
               addPostError = "Post already linked by this category";
            else if (category.postQuery != null)
               addPostError = "Post matched by post query";
            else
               System.err.println("**** Invalid case for updateSelectedPost");
         }
         else {
            postAddValid = true;
         }
      }
   }

   void addChildPost(BlogPost post, boolean addAsLink) {
      addPostError = null;
      addPostStatus = null;
      if (category == null)
         return;
      if (addAsLink) {
         if (category.linkedPosts == null)
            category.linkedPosts = new BArrayList<BlogPost>();
         if (category.linkedPosts.contains(post)) {
            addPostError = "Already a linked post of this category";
            return;
         }
         category.linkedPosts.add(post);
      }
      else {
         // This will update the childPosts
         post.parentCategory = category;
         if (!category.childPosts.contains(post)) {
            System.err.println("*** Child post not added automatically when setting parent category");
            category.childPosts.add(post);
         }
      }
      category.validateAllPosts();

      addPostStatus = "Post " + post.name + " added";

      selectedPost = null;
      postAddValid = false;
      findPostsText = "";
   }

   void removeChildPost(BlogPost post) {
      addPostError = null;
      addPostStatus = null;
      if (category == null)
         return;
      if (category.childPosts != null) {
         if (category.childPosts.indexOf(post) != -1) {
            post.parentCategory = null;
            addPostStatus = "Child post removed from category";
            category.validateAllPosts();
            return;
         }
      }
      if (category.linkedPosts != null) {
         if (category.linkedPosts.remove(post)) {
            addPostStatus = "Linked post removed from category";
            category.validateAllPosts();
            return;
         }
      }
      addPostError = "Unable to remove post added by query - alter query or post data so it no longer matches.";
   }

   @Sync(syncMode=SyncMode.Disabled)
   static IBeanMapper[] postProperties = null;

   static void initPostProperties() {
      if (postProperties == null) {
         postProperties = DynUtil.getProperties(BlogPost.class);
      }
   }

   void updatePostQuerySuggestions(String pattern) {
      postQueryError = null;
      postQueryStatus = null;
      if (pattern == null || pattern.trim().length() == 0) {
         postQuerySuggestions = getDefaultPostQuerySuggestions();
      }
      else {
         Object parseRes = language.parseString(null, pattern, language.expression, true);
         if (parseRes instanceof ParseError) {
            postQueryError = parseRes.toString();
            postQuerySuggestions = getDefaultPostQuerySuggestions();
         }
         else {
            Object semValue = ParseUtil.nodeToSemanticValue(parseRes);
            if (semValue instanceof JavaSemanticNode) {
               JavaSemanticNode semNode = (JavaSemanticNode) semValue;

               HashSet<String> candidates = new HashSet<String>();

               int cres = semNode.suggestCompletions(null, BlogPost.class, null, pattern, pattern.length(), candidates, null, 20);
               if (cres != 0) {

               }

               List<String> res = new ArrayList<String>();
               for (String s:candidates) {
                  if (s.endsWith(")")) // Ignoring methods returned here
                     continue;
                  if (cres > 0) {
                     res.add(pattern.substring(0, cres) + s);
                  }
                  else
                     res.add(s);
               }
               postQuerySuggestions = res;
            }
            else {
               System.err.println("*** Unrecognized result from parse");
               postQuerySuggestions = null;
            }
         }
      }
   }

   void updatePostQuery(String query) {
      postQueryError = null;
      postQueryStatus = null;
      if (query == null || query.trim().length() == 0) {
         category.postQuery = null;
         category.validateAllPosts();
      }
      else {
         Object parseRes = language.parseString(null, query, language.expression, true);
         if (parseRes instanceof ParseError) {
            postQueryError = parseRes.toString();
            postQuerySuggestions = getDefaultPostQuerySuggestions();
         }
         else {
            Object semValue = ParseUtil.nodeToSemanticValue(parseRes);
            if (semValue instanceof Expression) {
               Expression expr = (Expression) semValue;

               Object res = DBProvider.convertExpressionToQuery(BlogPost.class, expr, null, null);
               if (res instanceof Query) {
                  category.postQuery = (Query) res;
                  category.validateAllPosts();
                  postQueryStatus = "Updated post query";
               }
               else if (res == null) {
                  category.postQuery = null;
                  postQueryError = "Null return from expression query";
               }
               else
                  postQueryError = res.toString();
            }
         }
      }
   }

   List<String> getDefaultPostQuerySuggestions() {
      initPostProperties();
      List<String> res = new ArrayList<String>();
      for (IBeanMapper mapper:postProperties) {
         if (mapper == null)
            continue;
         String name = mapper.getPropertyName();
         if (!excludePropNames.contains(name))
            res.add(name);
      }
      return res;
   }


   void validateElement() {
      super.validateElement();
      if (category != null) {
         if (category.postQuery != null)
            postQueryText = category.postQuery.toString();
         else
            postQueryText = "";
      }
   }

   void validateCategoryPathName() {
      if (categoryPathName == null && category == null)
         return;
      if (categoryPathName != null && category != null && categoryPathName.equals(category.pathName))
         return;
      // Want to be sure the store path name has been set and the store updated if we are updating the skuCode and store name from the URL
         DynUtil.invokeLater(new Runnable() {
            void run() {
               if (categoryPathName == null && category != null)
                  doSelectCategory(null);
               else if (categoryPathName != null && (category == null || !category.pathName.equals(categoryPathName)) && siteMgr.site != null) {
                  List<BlogCategory> foundCategories = (List<BlogCategory>) BlogCategory.findByPathName(categoryPathName, siteMgr.site, 0, 1);
                  if (foundCategories != null && foundCategories.size() > 0)
                     doSelectCategory(foundCategories.get(0));
               }
            }
         }, 0);
   }
}
