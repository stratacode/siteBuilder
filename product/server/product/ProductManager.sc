import java.util.Arrays;
import java.util.TreeSet;

import sc.lang.html.HTMLElement;

import sc.util.BTreeMap;

ProductManager {
   productPathName =: validateProductPathName();
   skuCode =: validateSkuCode();

   List<Product> searchForText(String text) {
      return (List<Product>) Product.getDBTypeDescriptor().searchQuery(text, searchStore, getSearchStoreValues(), null, searchOrderBy, -1, -1);
   }

   void doSearch() {
      String txt = searchText == null ? "" : searchText;
      productList = searchForText(txt);
      if (productList.size() == 0) {
         int numProducts = Product.getDBTypeDescriptor().searchCountQuery("", searchStore, getSearchStoreValues());
         if (numProducts == 0)
            searchStatusMessage = "No products in store";
         else
            searchStatusMessage = "No products found out of: " + numProducts + " in store";
      }
   }

   void doSearchAll() {
      searchText = "";
      doSearch();
   }

   void doSearchRecent() {
      searchText = "";
      List<Product> recentProducts = (List<Product>) Product.getDBTypeDescriptor().query(Query.and(Query.eq("store", store), Query.gt("lastModified", new Date(System.currentTimeMillis() - recentMillis))), null, searchOrderBy, -1, -1);
      productList = recentProducts;
      if (productList.size() == 0) {
         int numProducts = Product.getDBTypeDescriptor().searchCountQuery("", searchStore, getSearchStoreValues());
         if (numProducts == 0)
            searchStatusMessage = "No products in store";
         else
            searchStatusMessage = "No products changed in last " + recentDays + " days out of: " + numProducts + " in store";
      }
   }

   void doSelectProduct(Product toSel) {
      clearFormErrors();
      // We might have just removed this product so don't make it current again
      if (((DBObject)toSel.getDBObject()).isActive()) {
         if (toSel == product) {
            element = null;
            sku = null;
            skuEditable = false;
            showSkuView = false;
            optionScheme = null;
            optionMediaFilter = null;
            editableOptionScheme = false;
            productSaved = false;

            categorySaved = false;
            category = null;
            showCategoryView = false;
            categoryEditable = false;

            parentCategoryPathName = "";
         }
         else {
            element = toSel;
            showSkuView = false;
            sku = product.sku;
            if (sku instanceof PhysicalSku)
               psku = (PhysicalSku) sku;

            if (sku != null && ((DBObject)sku.getDBObject()).isActive()) {
               skuEditable = true;
               optionScheme = sku.optionScheme;
               refreshOptionScheme();
               if (optionScheme != null)
                  editableOptionScheme = true;
            }
            else
               skuEditable = false;
            productSaved = true;
            category = product.parentCategory;
            if (category != null && ((DBObject) category.getDBObject()).isActive()) {
               categoryEditable = true;
               categorySaved = true;
            }
            else
               categoryEditable = false;

            parentCategoryPathName = category == null ? "" : category.pathName;
            showOptionsView = sku.optionScheme != null;
         }
      }
   }

   void validateProductPathName() {
      if (productPathName == null && product == null)
         return;
      if (productPathName != null && product != null && productPathName.equals(product.pathName))
         return;
      // Want to be sure the store path name has been set and the store updated if we are updating the skuCode and store name from the URL
         DynUtil.invokeLater(new Runnable() {
            void run() {
               if (productPathName == null && product != null)
                  doSelectProduct(null);
               else if (productPathName != null && (product == null || !product.pathName.equals(productPathName)) && siteMgr.store != null) {
                  List<Product> foundProducts = (List<Product>) Product.findByPathName(productPathName, siteMgr.store, 0, 1);
                  if (foundProducts != null && foundProducts.size() > 0)
                     doSelectProduct(foundProducts.get(0));
               }
            }
         }, 0);
   }

   void refreshOptionScheme() {
      refreshSkuOptions();
      refreshMediaFilter();
   }

   void updateMediaFilter(int ix, String val) {
      validateMediaFilter();
      optionMediaFilter.set(ix, val);
   }

   void validateMediaFilter() {
      if (optionMediaFilter == null && optionScheme != null)
         refreshMediaFilter();
   }

   void refreshMediaFilter() {
      if (optionScheme == null)
         optionMediaFilter = null;
      else {
         List<String> newFilter = optionScheme.defaultOptionFilter;
         if (!newFilter.equals(optionMediaFilter)) {
            optionMediaFilter = newFilter;
         }
         if (optionScheme.options != null) {
            for (ProductOption opt: optionScheme.options) {
               if (opt != null)
                  opt.refreshOptionFilterList();
            }
         }
      }
   }

   String getMediaFilterPattern() {
      if (optionScheme == null)
         return null;
      validateMediaFilter();
      int numOpts = optionScheme.options.size();
      StringBuilder res = new StringBuilder();
      boolean any = false;
      for (int i = 0; i < numOpts; i++) {
         ProductOption opt = optionScheme.options.get(i);
         String filter = optionMediaFilter.get(i);
         if (filter.equals(opt.anyString))
            continue;
         if (any)
            res.append(",");
         res.append(opt.optionName);
         res.append("=");
         res.append(optionMediaFilter.get(i));
         any = true;
      }
      if (!any)
         return null;
      return res.toString();
   }

   void removeProduct(long productId) {
      clearFormErrors();
      if (productId == 0) {
         errorMessage = "Invalid product id in remove";
         return;
      }
      Product prod = (Product) Product.getDBTypeDescriptor().findById(productId);
      if (prod == null) {
         errorMessage = "Product not found to remove";
         System.err.println("*** removeProduct - product with id: " + productId + " not found");
         return;
      }
      List<Object> propValues = Arrays.asList(prod);
      List<LineItem> refsToProd = (List<LineItem>) LineItem.getDBTypeDescriptor().findBy(Arrays.asList("product"), propValues, null, null, 0, 1);
      if (refsToProd != null && refsToProd.size() > 0) {
         errorMessage = "Unable to remove product that's in a shopping cart or order";
         return;
      }
      errorMessage = null;
      try {
         int toRemIx = productList == null ? -1 : productList.indexOf(prod);
         element = null;
         Category oldCat = prod.parentCategory;
         if (oldCat != null) {
            prod.parentCategory = null; // Will remove this from category.products
            oldCat.validateAllProducts();
         }
         prod.dbDelete(false);

         if (toRemIx != -1) {
            ArrayList<Product> newList = new ArrayList<Product>();
            for (int i = 0; i < productList.size(); i++) {
               if (i != toRemIx)
                  newList.add(productList.get(i));
            }
            productList = newList;
         }
      }
      catch (IllegalArgumentException exc) {
         errorMessage = "Failed to remove product: " + exc;
         return;
      }
   }

   void startAddProduct(boolean doCopy) {
      if (addInProgress)
         return;

      clearFormErrors();
      productSaved = false;

      Product newProd = (Product) Product.getDBTypeDescriptor().createInstance();
      if (product != null && doCopy) {
         newProd.sku = product.sku;
         newProd.name = "copy of " + product.name;
         newProd.pathName = product.pathName;
         newProd.mainMedia = product.mainMedia;
         newProd.shortDesc = product.shortDesc;
         newProd.longDesc = product.longDesc;
      }
      else {
         newProd.name = "";
         newProd.pathName = "";
      }
      if (newProd.shortDesc == null)
         newProd.shortDesc = "";
      if (newProd.longDesc == null)
         newProd.longDesc = "";
      if (newProd.sku != null) {
         sku = newProd.sku;
         optionScheme = sku.optionScheme;
         showOptionsView = optionScheme != null;
      }
      else {
         optionScheme = null;
         sku = null;
         showOptionsView = false;
      }
      element = newProd;

      // Automatically set this based on the product name unless it's already specified
      autoUpdatePath = product.pathName == null || product.pathName.length() == 0;

      addInProgress = true;
      skuEditable = product.sku != null;
      skuAddErrorMessage = skuFindErrorMessage = skuStatusMessage = null;

      parentCategoryPathName = "";
   }

   void startAddSku() {
      if (addSkuInProgress)
         return;
      initTemporarySku();
      addSkuInProgress = true;
      showSkuView = true;
      skuStatusMessage = null;
      skuAddErrorMessage = null;
      skuFindErrorMessage = null;
   }

   void initTemporarySku() {
      sku = (Sku) Sku.getDBTypeDescriptor().createInstanceOfType(skuTypeId);
      sku.store = store;
      sku.skuCode = "";
      if (sku instanceof PhysicalSku)
         psku = (PhysicalSku) sku;
      else
         psku = null;
   }

   void completeAddSku() {
      if (!addSkuInProgress)
         return;

      try {
         sku.validateSku();
         if (sku.propErrors == null) {
            sku.dbInsert(false);
            product.sku = sku;
            addSkuInProgress = false;
            showSkuView = false;
            skuStatusMessage = "Sku added";
            skuAddErrorMessage = skuFindErrorMessage = null;
            skuEditable = true;
         }
         else {
            skuAddErrorMessage = sku.formatErrors();
            skuStatusMessage = null;
            skuFindErrorMessage = null;
         }
      }
      catch (IllegalArgumentException exc) {
         skuStatusMessage = null;
         skuAddErrorMessage = "System error: " + exc;
         skuFindErrorMessage = null;
      }
   }

   void cancelAddSku() {
      addSkuInProgress = false;
      sku = null;
      skuAddErrorMessage = null;
      skuFindErrorMessage = null;
      skuStatusMessage = "Add sku cancelled";
      showSkuView = false;
   }

   void doneEditingSku() {
      addSkuInProgress = false;
      skuAddErrorMessage = null;
      skuFindErrorMessage = null;
      skuStatusMessage = null;
      showSkuView = false;
      if (product == null)
         sku = null;
   }

   void completeAddProduct() {
      clearFormErrors();
      if (!product.validateProperties()) {
         errorMessage = product.formatErrors();
         productSaved = true;
         return;
      }

      if (store == null) {
         errorMessage = "No current store";
         return;
      }

      try {
         if (addInProgress) {
            product.store = store;
            product.dbInsert(false);

            String newName = product.pathName;

            // This after we have inserted it to prevent the category from trying to insert the product due to the
            // bi-directional parentCategory/products relationships
            if (category != null)
               product.parentCategory = category;

            ArrayList<Product> newList = new ArrayList<Product>();
            newList.add(product);
            productList = newList;
            addInProgress = false;
            element = null;
            statusMessage = "Product " + newName + " created";
         }
         /*
         else {
            product.dbUpdate();
            statusMessage = "Changes saved";
         }
         */
      }
      catch (IllegalArgumentException exc) {
         errorMessage = "New product failed due to system error: " + exc;
      }
   }

   void cancelAddProduct() {
      addInProgress = false;
      resetForm();
   }

   void doneEditingProduct() {
      clearFormErrors();
      if (!product.validateProperties()) {
         errorMessage = product.formatErrors();
         productSaved = true;
         return;
      }
      element = null;
      productSaved = false;
      clearFormErrors();
   }

   void updateSkuType(int typeId) {
      if (typeId == skuTypeId)
         return;

      if (!sku.getDBObject().isTransient()) {
         skuAddErrorMessage = "Unable to change type of sku once it's been added";
         return;
      }
      skuTypeId = typeId;

      String saveCode = null; // TODO: add other sku attributes to save across a type change
      BigDecimal savePrice = null;
      BigDecimal saveDiscount = null;
      OptionScheme saveScheme = null;
      if (sku != null) {
         saveCode = sku.skuCode;
         saveDiscount = sku.discountPrice;
         saveScheme = sku.optionScheme;
      }
      initTemporarySku();
      if (saveCode != null && saveCode.length() > 0)
         sku.skuCode = saveCode;
      if (savePrice != null)
         sku.price = savePrice;
      if (saveDiscount != null)
         sku.discountPrice = saveDiscount;
      if (saveScheme != null)
         sku.optionScheme = saveScheme;

      refreshSkuOptions();
   }

   void updateManageInventory(boolean enable) {
      if (psku == null)
         return;
      if (enable) {
         psku.inventory = new ProductInventory();
      }
      else
         psku.inventory = null;
   }

   static final List<String> searchSkus = Arrays.asList("store", "mainSku");

   List<Object> getSearchSkusValues() {
      ArrayList<Object> res = new ArrayList<Object>();
      res.add(store);
      res.add(true); // returning mainSkus only since we get the rest via skuOptions
      return res;
   }

   void doSearchSkus(String pattern) {
      skuSearchText = pattern;
      if (pattern == null)
         pattern = "";
      List<Sku> allMatches = (List<Sku>) Sku.getDBTypeDescriptor().searchQuery(pattern, searchSkus, searchSkusValues, null, searchOrderBy, 0, 20);
      matchingSkus = filterMatchingSkus(allMatches);
   }

   List<Sku> filterMatchingSkus(List<Sku> allMatches) {
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<Sku> res = new ArrayList<Sku>();
      for (Sku match:allMatches) {
         if (!found.contains(match.skuCode)) {
            res.add(match);
            found.add(match.skuCode);
            if (res.size() == 10)
               break;
         }
      }
      return res;
   }

   void doSearchAllSkus() {
      doSearchSkus("");
   }

   void doSearchRecentSkus() {
      skuSearchText = "";
      List<Sku> recentSkus = (List<Sku>) Sku.getDBTypeDescriptor().query(Query.and(Query.eq("store", store), Query.gt("lastModified", new Date(System.currentTimeMillis() - recentDays * 24 * 60 * 60 * 1000))), null, searchOrderBy, -1, -1);
      matchingSkus = filterMatchingSkus(recentSkus);
   }

   void updateProductSku(String skuCode) {
      skuFindErrorMessage = null;
      skuAddErrorMessage = null;
      skuStatusMessage = null;

      if (skuCode == null || skuCode.trim().length() == 0) {
         product.sku = null;
         skuEditable = false;
         return;
      }

      List<Sku> skus = Sku.findBySkuCode(skuCode, store);
      if (skus != null && skus.size() > 0) {
         Sku newSku = skus.get(0);
         product.sku = newSku;
         sku = newSku;
         optionScheme = newSku.optionScheme;
         refreshMediaFilter();
         refreshSkuOptions();
         skuEditable = true;

         skuStatusMessage = "Sku updated to: " + skuCode;

         showOptionsView = optionScheme != null;
      }
      else {
         skuEditable = false;
         skuFindErrorMessage = "No sku with skuCode: " + skuCode;
      }
   }

   void updateMatchingCategories(String pattern) {
      matchingCategories = getMatchingCategories(store, pattern);
   }

   void updateParentCategory(String pathName) {
      parentCategoryPathName = pathName;
      if (pathName == null || pathName.trim().length() == 0) {
         product.parentCategory = null;
         if (category != null)
            category.validateAllProducts();
         category = null;
         product.removePropError("parentCategory");
         return;
      }

      List<Category> cats = Category.findByPathName(pathName, store, 0, 10);
      boolean newProduct = product.getDBObject().isTransient();
      if (cats != null && cats.size() > 0) {
         if (!newProduct)
            product.parentCategory = cats.get(0);
         product.removePropError("parentCategory");
         // Not setting product.parentCategory here for new products because it ends up inserting the product
         // because of the bi-directional relationship - the category is already added so once we set the product
         // to point to the category, the category wants to point to the product and adds it.
         category = cats.get(0);
         category.validateAllProducts();
      }
      else {
         product.addPropError("parentCategory", "No category with path name: " + pathName);
      }
   }

   // Options view

   void startNewOptionScheme() {
      optionScheme = (OptionScheme) OptionScheme.getDBTypeDescriptor().createInstance();
      optionScheme.schemeName = product == null ? "" : product.pathName;
      optionScheme.store = store;

      optionScheme.options = new ArrayList<ProductOption>();
      addNewOption();

      showOptionSchemeView = true;

      refreshMediaFilter();

      optionSchemeSaved = false;
   }

   void doEditOptionScheme() {
      if (!editableOptionScheme) {
         System.err.println("*** doEditOptionScheme called without editable option");
         return;
      }
      optionErrorMessage = optionStatusMessage = null;
      showOptionSchemeView = true;
      optionSchemeSaved = true;
   }

   void updateMatchingOptionSchemes(String pattern) {
      if (pattern == null)
         pattern = "";
      List<OptionScheme> allMatches = (List<OptionScheme>) OptionScheme.getDBTypeDescriptor().searchQuery(pattern, null, null, null, searchOrderBy, 0, 20);
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<OptionScheme> res = new ArrayList<OptionScheme>();
      for (OptionScheme match:allMatches) {
         if (!found.contains(match.schemeName)) {
            res.add(match);
            found.add(match.schemeName);
            if (res.size() == 10)
               break;
         }
      }
      matchingOptionSchemes = res;
   }

   void updateOptionScheme(String optionSchemeName) {
      if (optionSchemeName == null || optionSchemeName.trim().length() == 0) {
         if (sku.optionScheme != null) {
            sku.optionScheme = null;
            optionStatusMessage = "Cleared option scheme";

            refreshMediaFilter();
         }
         return;
      }

      List<OptionScheme> schemes = OptionScheme.findBySchemeName(optionSchemeName, store);
      OptionScheme newScheme = null;
      if (schemes != null) {
         if (schemes.size() == 1) {
            newScheme = schemes.get(0);
         }
         else if (schemes.size() > 1) {
            newScheme = schemes.get(0);
         }
      }
      if (newScheme == null) {
         optionStatusMessage = null;
         optionErrorMessage = "No option scheme with scheme name: " + optionSchemeName;
      }
      else {
         sku.optionScheme = newScheme;
         optionScheme = newScheme;
         optionStatusMessage = "Option scheme updated to: " + optionSchemeName + " for sku: " + sku.skuCode;

         refreshSkuOptions();
      }
   }

   void updateHasOptions(boolean status) {
      if (sku == null)
         return;
      if (!status) {
         sku.optionScheme = null;
         optionScheme = null;
      }
      showOptionsView = status;
      refreshMediaFilter();
   }

   void addNewOption() {
      ProductOption opt = (ProductOption) ProductOption.getDBTypeDescriptor().createInstance();
      opt.optionName = "";
      opt.optionValues = new ArrayList<OptionValue>();
      opt.defaultValue = addNewOptionValue(opt, false);
      optionScheme.options.add(opt);
      newProductOption = opt;
      opt.dbInsert(false);
   }

   void removeOption(ProductOption option) {
      if (!optionScheme.options.remove(option))
         System.err.println("*** Error - unable to find option to remove");
   }

   OptionValue addNewOptionValue(ProductOption option, boolean setFocus) {
      OptionValue optVal = new OptionValue();
      optVal.optionValue = "";
      optVal.skuSymbol = "";
      option.optionValues.add(optVal);
      if (setFocus)
         newOptionValue = optVal;
      optVal.dbInsert(false);
      return optVal;
   }

   void removeOptionValue(ProductOption option, OptionValue optVal) {
      boolean isDefault = option.defaultValue == optVal;
      if (!option.optionValues.remove(optVal))
         System.err.println("*** Error - no option value found to remove");
      else {
         option.refreshOptionFilterList();
         if (isDefault && option.optionValues.size() > 0)
            option.defaultValue = option.optionValues.get(0);
      }
   }

   void completeNewOptionScheme() {
      if (!validateOptionScheme()) {
         return;
      }
      optionScheme.dbInsert(false);
      sku.optionScheme = optionScheme;
      showOptionSchemeView = false;
      optionStatusMessage = "Option scheme: " + optionScheme.schemeName + " added";
      optionErrorMessage = null;
      refreshMediaFilter();
      refreshSkuOptions();
      editableOptionScheme = true;
      optionSchemeSaved = true;
   }

   void validateOptionSchemeName() {
      if (optionSchemeSaved)
         validateOptionScheme();
      optionScheme.validateProp("schemeName");
   }

   boolean validateOptionScheme() {
      optionScheme.validateOptionScheme();
      optionSchemeSaved = true;
      if (optionScheme.hasErrors()) {
         optionStatusMessage = null;
         optionErrorMessage = optionScheme.formatErrors();
         return false;
      }
      return true;
   }

   void cancelNewOptionScheme() {
      optionScheme = sku == null ? null : sku.optionScheme;
      showOptionSchemeView = false;
      refreshMediaFilter();
      refreshSkuOptions();
      editableOptionScheme = sku.optionScheme != null;
      optionStatusMessage = optionErrorMessage = null;
      optionSchemeSaved = false;
   }

   void doneEditingOptionScheme() {
      if (!validateOptionScheme())
         return;
      optionScheme.dbUpdate();
      showOptionSchemeView = false;
      optionStatusMessage = "Option scheme: " + optionScheme.schemeName + " saved";
      optionSchemeSaved = true;
   }

   void refreshSkuOptions() {
      missingSkuOptions = new ArrayList<Sku>();
      validSkuOptions = new ArrayList<Sku>();
      invalidSkuOptions = new ArrayList<Sku>();
      if (sku.optionScheme != null) {
         sku.verifySkuOptions(validSkuOptions, missingSkuOptions, invalidSkuOptions);
      }
   }

   void removeInvalidSku(Sku toRem) {
      if (invalidSkuOptions.contains(toRem)) {
         if (sku.skuOptions.remove(toRem)) {
            // TODO: delete the sku here here if there are no orders or anything pointing to it
            invalidSkuOptions.remove(toRem);
         }
      }
   }

   void addMissingSku(Sku newSkuOpt) {
      if (missingSkuOptions.contains(newSkuOpt)) {
         if (sku.skuOptions == null || !sku.skuOptions.contains(newSkuOpt)) {
            if (!((DBObject)sku.getDBObject()).isTransient())
               newSkuOpt.dbInsert(false);
            sku.addSkuOption(newSkuOpt);
            missingSkuOptions.remove(newSkuOpt);
            validSkuOptions.add(newSkuOpt);
         }
      }
   }

   void removeSkuOption(Sku skuOption) {
      if (sku != null && sku.skuOptions != null) {
         if (!sku.skuOptions.remove(skuOption)) {
            System.err.println("*** Did not find sku option to remove");
            return;
         }
         refreshSkuOptions();
      }
   }

   void clearSkuMessages() {
      skuAddErrorMessage = skuFindErrorMessage = skuStatusMessage = null;
   }

   void updateSkuCode(String value) {
      String err = Sku.validateSkuCode(value);
      if (err != null) {
         sku.addPropError("skuCode", err);
         return;
      }
      else {
         sku.removePropError("skuCode");
         clearSkuMessages();
      }
      sku.skuCode = value;
      if (sku.skuOptions != null)
         updateSkuOptionsForCode(sku.skuOptions, value);
      if (missingSkuOptions != null)
         updateSkuOptionsForCode(missingSkuOptions, value);
   }

   void updateSkuOptionsForCode(List<Sku> optionList, String mainSkuCode) {
      if (optionList != null) {
         for (Sku optionSku:optionList) {
            optionSku.skuCode = sku.getSkuOptionCode(optionSku.options, mainSkuCode);
         }
      }
   }

   void newCategoryCompleted(Category cat) {
      if (!addInProgress) {
         product.parentCategory = cat;
         if (cat != null)
            cat.validateAllProducts();
      }
      product.removePropError("parentCategory");
      parentCategoryPathName = cat.pathName;
   }

   void doSelectSkuFromCode(String skuCode) {
      if (skuCode != null && skuCode.length() > 0 && store != null) {
         if (sku != null && sku.skuCode.equals(skuCode))
            return;
         List<Sku> newCurSku = Sku.findBySkuCode(skuCode, store);
      }
   }

   void doSelectSku(Sku toSel) {
      clearFormErrors();
      // We might have just removed this product so don't make it current again
      if (((DBObject)toSel.getDBObject()).isActive()) {
         if (toSel == sku) {
            sku = null;
            skuEditable = false;
            showSkuView = false;
            optionScheme = null;
            editableOptionScheme = false;
            showOptionsView = false;
            skuCode = "";
         }
         else {
            sku = toSel;
            if (sku instanceof PhysicalSku)
               psku = (PhysicalSku) sku;

            skuEditable = true;
            optionScheme = sku.optionScheme;
            refreshOptionScheme();
            editableOptionScheme = true;

            showSkuView = true;
            showOptionsView = optionScheme != null;

            if (sku.skuCode != null)
               skuCode = sku.skuCode;
         }
      }
   }

   void validateSkuCode() {
      if (skuCode == null && sku == null)
         return;
      if (skuCode != null && sku != null && skuCode.equals(sku.skuCode))
         return;
      // Want to be sure the store path name has been set and the store updated if we are updating the skuCode and store name from the URL
      DynUtil.invokeLater(new Runnable() {
         void run() {
            if (skuCode == null && sku != null)
               doSelectSku(null);
            else if (skuCode != null && (sku == null || !sku.skuCode.equals(skuCode)) && siteMgr.store != null) {
               List<Sku> foundSkus = (List<Sku>) Sku.findBySkuCode(skuCode, siteMgr.store);
               if (foundSkus != null && foundSkus.size() > 0)
                  doSelectSku(foundSkus.get(0));
            }
         }
      }, 0);
   }

   void removeSku(long skuId) {
      clearFormErrors();
      if (skuId == 0) {
         skuAddErrorMessage = "Invalid sku id in remove";
         return;
      }
      Sku toRem = (Sku) Sku.getDBTypeDescriptor().findById(skuId);
      if (toRem == null) {
         skuAddErrorMessage = "Sku not found to remove";
         System.err.println("*** removeSku - sku with id: " + skuId + " not found");
         return;
      }
      // Security check to be sure we're allowed to manage this sku since we did the lookup by ID only
      if (toRem.store != store) {
         skuAddErrorMessage = "Sku not part of current store";
         System.err.println("*** removeSku - sku with bad store: " + skuId + " not found");
         return;
      }
      List<Object> propValues = Arrays.asList(sku);
      List<Product> refsToProd = (List<Product>) Product.getDBTypeDescriptor().findBy(Arrays.asList("sku"), propValues, null, null, 0, 1);
      if (refsToProd != null && refsToProd.size() > 0) {
         errorMessage = "Unable to remove sku that's referenced by a product";
         return;
      }
      errorMessage = null;
      try {
         int toRemIx = matchingSkus == null ? -1 : matchingSkus.indexOf(toRem);
         element = null;
         sku.dbDelete(false);

         if (toRemIx != -1) {
            ArrayList<Sku> newList = new ArrayList<Sku>();
            for (int i = 0; i < matchingSkus.size(); i++) {
               if (i != toRemIx)
                  newList.add(matchingSkus.get(i));
            }
            matchingSkus = newList;
         }
      }
      catch (IllegalArgumentException exc) {
         errorMessage = "Failed to remove sku: " + exc;
         return;
      }
   }
}
