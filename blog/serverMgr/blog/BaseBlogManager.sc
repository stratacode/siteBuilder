import java.util.TreeSet;
import java.util.Arrays;

import sc.lang.html.HTMLElement;

BaseBlogManager {
   longDescHtml =: updateLongDesc(longDescHtml);

   void siteChanged() {
      resetForm();
   }

   void updateLongDesc(String htmlText) {
      if (element == null)
         return;
      String error = HTMLElement.validateClientHTML(htmlText, HTMLElement.formattingTags, HTMLElement.formattingAtts);
      if (error == null)
         blogElement.longDesc = htmlText;
      else
         System.err.println("Invalid html text submission: " + htmlText + ": " + error);
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
      category.shortDesc = "";
      category.longDesc = "";
   }

   abstract void newCategoryCompleted(BlogCategory category);

   void completeAddCategory() {
      if (!addCategoryInProgress)
         return;

      try {
         clearFormErrors();
         category.validateProperties();
         if (category.propErrors == null) {
            category.site = site;
            category.dbInsert(false);

            addCategoryInProgress = false;
            showCategoryView = false;
            categoryStatusMessage = "Blog category added";
            categoryErrorMessage = null;
            categoryEditable = true;

            newCategoryCompleted(category);
         }
         else {
            categoryErrorMessage = category.formatErrors();
            categoryStatusMessage = null;
         }
      }
      catch (IllegalArgumentException exc) {
         categoryStatusMessage = null;
         categoryErrorMessage = "System error: " + exc;
      }
   }

   void cancelAddCategory() {
      addCategoryInProgress = false;
      category = null;
      categoryErrorMessage = null;
      categoryStatusMessage = "Add blog category cancelled";
      showCategoryView = false;
   }

   void doneEditingCategory() {
      addCategoryInProgress = false;
      categoryErrorMessage = null;
      categoryStatusMessage = null;
      showCategoryView = false;
   }

   static List<BlogCategory> getMatchingCategories(SiteContext site, String pattern) {
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<BlogCategory> res = new ArrayList<BlogCategory>();
      ArrayList<Object> searchValues = new ArrayList<Object>();
      searchValues.add(site);
      List<BlogCategory> allMatches = (List<BlogCategory>) BlogCategory.getDBTypeDescriptor().searchQuery(pattern, searchSite, searchValues, null, searchOrderBy, 0, 20);
      for (BlogCategory match:allMatches) {
         if (!found.contains(match.pathName)) {
            res.add(match);
            found.add(match.pathName);
            if (res.size() == 10)
               break;
         }
      }
      return res;
   }

   static List<BlogPost> getMatchingPosts(SiteContext site, String pattern) {
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<BlogPost> res = new ArrayList<BlogPost>();
      ArrayList<Object> searchValues = new ArrayList<Object>();
      searchValues.add(site);
      List<BlogPost> allMatches = (List<BlogPost>) BlogPost.getDBTypeDescriptor().searchQuery(pattern, searchSite, searchValues, null, searchOrderBy, 0, 20);
      for (BlogPost match:allMatches) {
         if (!found.contains(match.pathName)) {
            res.add(match);
            found.add(match.pathName);
            if (res.size() == 10)
               break;
         }
      }
      return res;
   }
}
