import java.util.Arrays;
import java.util.TreeSet;

import sc.lang.html.HTMLElement;

CategoryManager {
   List<Category> searchForText(String text) {
      return (List<Category>) Category.getDBTypeDescriptor().searchQuery(text, searchStore, getSearchStoreValues(), null, searchOrderBy, -1, -1);
   }

   void doSearch() {
      String txt = searchText == null ? "" : searchText;
      categoryList = searchForText(txt);
   }

   void doSelectCategory(Category toSel) {
      clearFormErrors();
      // We might have just removed this product so don't make it current again
      if (((DBObject)toSel.getDBObject()).isActive()) {
         if (toSel == category) {
            element = null;
            categorySaved = false;
            category = null;
            showCategoryView = false;
            categoryEditable = false;
            parentCategoryPathName = "";
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
         }
      }
   }

   void removeCategory(long categoryId) {
      clearFormErrors();
      if (categoryId == 0) {
         categoryErrorMessage = "Invalid category id in remove";
         return;
      }
      Category cat = (Category) Category.getDBTypeDescriptor().findById(categoryId);
      if (cat == null) {
         categoryErrorMessage = "Category not found to remove";
         System.err.println("*** removeCategory - product with id: " + categoryId + " not found");
         return;
      }
      List<Object> propValues = Arrays.asList(cat);
      List<Product> prodRefs = (List<Product>) Product.getDBTypeDescriptor().findBy(Arrays.asList("parentCategory"), propValues, null, null, 0, 1);
      if (prodRefs != null && prodRefs.size() > 0) {
         categoryErrorMessage = "Unable to remove category that still has products: " + prodRefs;
         return;
      }
      List<Category> catRefs = (List<Category>) Category.getDBTypeDescriptor().findBy(Arrays.asList("parentCategory"), propValues, null, null, 0, 1);
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
            ArrayList<Category> newList = new ArrayList<Category>();
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

      Category newCat = (Category) Category.getDBTypeDescriptor().createInstance();
      if (category != null && doCopy) {
         newCat.name = "copy of " + category.name;
         newCat.pathName = category.pathName;
         newCat.mainMedia = category.mainMedia;
         newCat.shortDesc = category.shortDesc;
         newCat.longDesc = category.longDesc;
      }
      else {
         newCat.name = "";
         newCat.pathName = "";
      }
      if (newCat.shortDesc == null)
         newCat.shortDesc = "";
      if (newCat.longDesc == null)
         newCat.longDesc = "";
      element = newCat;

      // Automatically set this based on the product name unless it's already specified
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
      category = (Category) Category.getDBTypeDescriptor().createInstance();
      category.store = store;
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

   void newCategoryCompleted(Category newCategory) {
      if (parentCategory != null)
         newCategory.parentCategory = parentCategory;
      ArrayList<Category> newList = new ArrayList<Category>();
      newList.add(newCategory);
      categoryList = newList;
      element = null;
      parentCategoryPathName = "";
   }


   void updateMatchingCategories(String pattern) {
      if (pattern == null || pattern.length() < 2)
         return;
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<Category> res = new ArrayList<Category>();
      List<Category> allMatches = (List<Category>) Category.getDBTypeDescriptor().searchQuery(pattern, null, null, null, searchOrderBy, 0, 20);
      for (Category match:allMatches) {
         if (!found.contains(match.pathName)) {
            res.add(match);
            found.add(match.pathName);
            if (res.size() == 10)
               break;
         }
      }
      matchingCategories = res;
   }

   String checkParentCategory(Category current, Category newParent, int ix) {
      if (newParent == category) {
         if (ix == 0)
            return "Parent should be a different category";
         else
            return "New parent category is a child of the old one";
      }
      Category nextParent = newParent.parentCategory;
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

      List<Category> cats = Category.findByPathName(pathName, store, 0, 10);
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
         // Not setting product.parentCategory here for new products because it ends up inserting the product
         // because of the bi-directional relationship - the category is already added so once we set the product
         // to point to the category, the category wants to point to the product and adds it.
      }
      else
         category.addPropError("parentCategory", "No category with path name: " + pathName);
   }
}
