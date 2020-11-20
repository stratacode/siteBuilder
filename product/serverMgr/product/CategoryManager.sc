import java.util.Arrays;
import java.util.TreeSet;
import java.util.HashSet;

import sc.lang.html.HTMLElement;
import sc.lang.sql.DBProvider;

import sc.parser.ParseError;
import sc.parser.ParseUtil;
import sc.lang.java.JavaSemanticNode;
import sc.lang.java.Expression;

CategoryManager {
   categoryPathName =: validateCategoryPathName();

   List<Category> searchForText(String text) {
      return (List<Category>) Category.getDBTypeDescriptor().searchQuery(text, searchStore, getSearchStoreValues(), null, searchOrderBy, -1, -1);
   }

   void doSearch() {
      String txt = searchText == null ? "" : searchText;
      categoryList = searchForText(txt);
      if (categoryList.size() == 0) {
         int numCategories = Category.getDBTypeDescriptor().searchCountQuery("", searchStore, getSearchStoreValues());
         if (numCategories == 0)
            searchStatusMessage = "No categories in this store";
         else
            searchStatusMessage = "No categories found out of: " + numCategories + " in the store";
      }
   }

   void doSelectCategory(Category toSel) {
      clearFormErrors();
      // We might have just removed this product so don't make it current again
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
            category.validateAllProducts();
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
      super.cancelAddCategory();
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

   void updateMatchingProducts(String pattern) {
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<Product> res = new ArrayList<Product>();
      List<Product> allMatches = (List<Product>) Product.getDBTypeDescriptor().searchQuery(pattern, null, null, null, searchOrderBy, 0, 20);
      for (Product match:allMatches) {
         if (!found.contains(match.pathName)) {
            res.add(match);
            found.add(match.pathName);
            if (res.size() == 10)
               break;
         }
      }
      matchingProducts = res;
   }

   void updateSelectedProduct(String pathName) {
      addProductError = null;
      addProductStatus = null;
      List<Product> products = Product.findByPathName(pathName, store, 0, 1);
      if (products == null || products.size() == 0) {
         selectedProduct = null;
         productAddValid = false;
      }
      else {
         selectedProduct = products.get(0);
         if (category.allProducts != null && category.allProducts.contains(selectedProduct)) {
            productAddValid = false;
            if (selectedProduct.parentCategory == category)
               addProductError = "Product already a child of this category";
            else if (category.linkedProducts != null && category.linkedProducts.contains(selectedProduct))
               addProductError = "Product already linked by this category";
            else if (category.productQuery != null)
               addProductError = "Product matched by product query";
            else
               System.err.println("**** Invalid case for updateSelectedProduct");
         }
         else {
            productAddValid = true;
         }
      }
   }

   void addChildProduct(Product product, boolean addAsLink) {
      addProductError = null;
      addProductStatus = null;
      if (category == null)
         return;
      if (addAsLink) {
         if (category.linkedProducts == null)
            category.linkedProducts = new BArrayList<Product>();
         if (category.linkedProducts.contains(product)) {
            addProductError = "Already a linked product of this category";
            return;
         }
         category.linkedProducts.add(product);
      }
      else {
         // This will update the childProducts
         product.parentCategory = category;
         if (!category.childProducts.contains(product)) {
            System.err.println("*** Child product not added automatically when setting parent category");
            category.childProducts.add(product);
         }
      }
      category.validateAllProducts();

      addProductStatus = "Product " + product.name + " added";

      selectedProduct = null;
      productAddValid = false;
      findProductsText = "";
   }

   void removeChildProduct(Product product) {
      addProductError = null;
      addProductStatus = null;
      if (category == null)
         return;
      if (category.childProducts != null) {
         if (category.childProducts.indexOf(product) != -1) {
            product.parentCategory = null;
            addProductStatus = "Child product removed from category";
            category.validateAllProducts();
            return;
         }
      }
      if (category.linkedProducts != null) {
         if (category.linkedProducts.remove(product)) {
            addProductStatus = "Linked product removed from category";
            category.validateAllProducts();
            return;
         }
      }
      addProductError = "Unable to remove product added by query - alter query or product data so it no longer matches.";
   }

   @Sync(syncMode=SyncMode.Disabled)
   static IBeanMapper[] productProperties = null;

   static void initProductProperties() {
      if (productProperties == null) {
         productProperties = DynUtil.getProperties(Product.class);
      }
   }

   void updateProductQuerySuggestions(String pattern) {
      productQueryError = null;
      productQueryStatus = null;
      if (pattern == null || pattern.trim().length() == 0) {
         productQuerySuggestions = getDefaultProductQuerySuggestions();
      }
      else {
         Object parseRes = language.parseString(null, pattern, language.expression, true);
         if (parseRes instanceof ParseError) {
            productQueryError = parseRes.toString();
            productQuerySuggestions = getDefaultProductQuerySuggestions();
         }
         else {
            Object semValue = ParseUtil.nodeToSemanticValue(parseRes);
            if (semValue instanceof JavaSemanticNode) {
               JavaSemanticNode semNode = (JavaSemanticNode) semValue;

               HashSet<String> candidates = new HashSet<String>();

               int cres = semNode.suggestCompletions(null, Product.class, null, pattern, pattern.length(), candidates, null, 20);
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
               productQuerySuggestions = res;
            }
            else {
               System.err.println("*** Unrecognized result from parse");
               productQuerySuggestions = null;
            }
         }
      }
   }

   void updateProductQuery(String query) {
      productQueryError = null;
      productQueryStatus = null;
      if (query == null || query.trim().length() == 0) {
         category.productQuery = null;
         category.validateAllProducts();
      }
      else {
         Object parseRes = language.parseString(null, query, language.expression, true);
         if (parseRes instanceof ParseError) {
            productQueryError = parseRes.toString();
            productQuerySuggestions = getDefaultProductQuerySuggestions();
         }
         else {
            Object semValue = ParseUtil.nodeToSemanticValue(parseRes);
            if (semValue instanceof Expression) {
               Expression expr = (Expression) semValue;

               Object res = DBProvider.convertExpressionToQuery(Product.class, expr, null, null);
               if (res instanceof Query) {
                  category.productQuery = (Query) res;
                  category.validateAllProducts();
                  productQueryStatus = "Updated product query";
               }
               else if (res == null) {
                  category.productQuery = null;
                  productQueryError = "Null return from expression query";
               }
               else
                  productQueryError = res.toString();
            }
         }
      }
   }

   List<String> getDefaultProductQuerySuggestions() {
      initProductProperties();
      List<String> res = new ArrayList<String>();
      for (IBeanMapper mapper:productProperties) {
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
         if (category.productQuery != null)
            productQueryText = category.productQuery.toString();
         else
            productQueryText = "";
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
               else if (categoryPathName != null && (category == null || !category.pathName.equals(categoryPathName)) && siteMgr.store != null) {
                  List<Category> foundCategories = (List<Category>) Category.findByPathName(categoryPathName, siteMgr.store, 0, 1);
                  if (foundCategories != null && foundCategories.size() > 0)
                     doSelectCategory(foundCategories.get(0));
               }
            }
         }, 0);
   }
}
