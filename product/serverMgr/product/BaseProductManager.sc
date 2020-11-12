import java.util.TreeSet;
import java.util.Arrays;

import sc.lang.html.HTMLElement;

BaseProductManager {
   longDescHtml =: updateLongDesc(longDescHtml);

   static List<Category> getMatchingCategories(Storefront store, String pattern) {
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<Category> res = new ArrayList<Category>();
      ArrayList<Object> searchValues = new ArrayList<Object>();
      searchValues.add(store);
      List<Category> allMatches = (List<Category>) Category.getDBTypeDescriptor().searchQuery(pattern, searchStore, searchValues, null, searchOrderBy, 0, 20);
      for (Category match:allMatches) {
         if (!found.contains(match.pathName)) {
            res.add(match);
            found.add(match.pathName);
            if (res.size() == 10)
               break;
         }
      }
      return res;
   }

   static List<Product> getMatchingProducts(Storefront store, String pattern) {
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<Product> res = new ArrayList<Product>();
      ArrayList<Object> searchValues = new ArrayList<Object>();
      searchValues.add(store);
      List<Product> allMatches = (List<Product>) Product.getDBTypeDescriptor().searchQuery(pattern, searchStore, searchValues, null, searchOrderBy, 0, 20);
      for (Product match:allMatches) {
         if (!found.contains(match.pathName)) {
            res.add(match);
            found.add(match.pathName);
            if (res.size() == 10)
               break;
         }
      }
      return res;
   }

   void storeChanged() {
      resetForm();
   }

   void updateLongDesc(String htmlText) {
      if (element == null)
         return;
      String error = HTMLElement.validateClientHTML(htmlText, HTMLElement.formattingTags, HTMLElement.formattingAtts);
      if (error == null)
         catalogElement.longDesc = htmlText;
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
      category = (Category) Category.getDBTypeDescriptor().createInstance();
      category.store = store;
      category.name = "";
      category.pathName = "";
      category.shortDesc = "";
      category.longDesc = "";
   }

   abstract void newCategoryCompleted(Category category);

   void completeAddCategory() {
      if (!addCategoryInProgress)
         return;

      try {
         clearFormErrors();
         category.validateProperties();
         if (category.propErrors == null) {
            category.store = store;
            category.dbInsert(false);

            addCategoryInProgress = false;
            showCategoryView = false;
            categoryStatusMessage = "Category added";
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
      categoryStatusMessage = "Add category cancelled";
      showCategoryView = false;
   }

   void doneEditingCategory() {
      addCategoryInProgress = false;
      categoryErrorMessage = null;
      categoryStatusMessage = null;
      showCategoryView = false;
   }

}
