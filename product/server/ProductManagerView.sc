import java.util.Arrays;
import java.util.TreeSet;

import sc.lang.html.HTMLElement;

ProductManagerView {
   store =: storeChanged();

   longDescHtml =: updateLongDesc(longDescHtml);

   void storeChanged() {
      resetForm();
   }

   static final List<String> searchOrderBy = Arrays.asList("-lastModified");
   static final List<String> searchStore = Arrays.asList("store");

   List<Object> getSearchStoreValues() {
      ArrayList<Object> res = new ArrayList<Object>();
      res.add(store);
      return res;
   }

   List<Product> searchForText(String text) {
      return (List<Product>) Product.getDBTypeDescriptor().searchQuery(text, searchStore, getSearchStoreValues(), null, searchOrderBy, -1, -1);
   }

   void doSearch() {
      String txt = searchText == null ? "" : searchText;
      productList = searchForText(txt);
   }

   void doSelectProduct(Product toSel) {
      clearFormErrors();
      // We might have just removed this product so don't make it current again
      if (((DBObject)toSel.getDBObject()).isActive()) {
         if (toSel == product) {
            product = null;
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
         }
         else {
            product = toSel;
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
         }
      }
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
         int toRemIx = prod == null ? -1 : productList.indexOf(prod);
         product = null;
         if (prod.parentCategory != null)
            prod.parentCategory = null; // Will remove this from category.products
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
      product = newProd;

      // Automatically set this based on the product name unless it's already specified
      autoUpdatePath = product.pathName == null || product.pathName.length() == 0;

      addInProgress = true;
      skuEditable = product.sku != null;
      skuAddErrorMessage = skuFindErrorMessage = skuStatusMessage = null;
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
   }

   void completeAddCategory() {
      if (!addCategoryInProgress)
         return;

      try {
         category.validateProperties();
         if (category.propErrors == null) {
            category.dbInsert(false);

            if (!addInProgress)
               product.parentCategory = category;
            addCategoryInProgress = false;
            showCategoryView = false;
            categoryStatusMessage = "Sku added";
            categoryErrorMessage = null;
            categoryEditable = true;
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

   void doAddProduct() {
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
            product = null;
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
      product = null;
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

   void updateMatchingSkus(String pattern) {
      if (pattern == null || pattern.length() < 2)
         return;
      List<Sku> allMatches = (List<Sku>) Sku.getDBTypeDescriptor().searchQuery(pattern, searchStore, searchStoreValues, null, searchOrderBy, 0, 20);
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
      matchingSkus = res;
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

      List<Sku> skus = Sku.findBySkuCode(skuCode);
      if (skus != null && skus.size() > 0) {
         Sku newSku = skus.get(0);
         product.sku = newSku;
         sku = newSku;
         optionScheme = newSku.optionScheme;
         refreshMediaFilter();
         refreshSkuOptions();
         skuEditable = true;

         skuStatusMessage = "Sku updated to: " + skuCode;
      }
      else {
         skuEditable = false;
         skuFindErrorMessage = "No sku with skuCode: " + skuCode;
      }
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

   void updateParentCategory(String pathName) {
      if (pathName == null || pathName.trim().length() == 0) {
         product.parentCategory = null;
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
      }
      else
         product.addPropError("parentCategory", "No category with path name: " + pathName);
   }

   void updateMatchingMedia(String pattern) {
      if (pattern == null || pattern.length() < 2)
         return;
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<ManagedMedia> res = new ArrayList<ManagedMedia>();
      List<ManagedMedia> allMatches = (List<ManagedMedia>) ManagedMedia.getDBTypeDescriptor().searchQuery(pattern, null, null, null, searchOrderBy, 0, 20);
      for (ManagedMedia match:allMatches) {
         if (!found.contains(match.uniqueFileName)) {
            res.add(match);
            found.add(match.uniqueFileName);
            if (res.size() == 10)
               break;
         }
      }
      matchingMedia = res;
   }

   void updateProductMedia(String uniqueFileName) {
      String fileName = MediaManager.removeRevisionFromFile(uniqueFileName);
      List<ManagedMedia> mediaList = ManagedMedia.findByFileName(fileName);
      mediaStatusMessage = null;
      mediaErrorMessage = null;
      findMediaText = uniqueFileName;

      String newFilter = getMediaFilterPattern();

      if (mediaList != null) {
         for (ManagedMedia media:mediaList) {
            if (media.uniqueFileName.equals(uniqueFileName)) {
               List<ManagedMedia> prodMedia = product.altMedia;
               boolean setList = false;
               if (prodMedia == null) {
                  prodMedia = new ArrayList<ManagedMedia>();
                  setList = true;
               }
               else if (prodMedia.contains(media)) {
                  if (newFilter != null) {
                     String oldFilter = media.filterPattern;
                     if (oldFilter != null && !oldFilter.equals(newFilter)) {
                        mediaStatusMessage = "Changed filter pattern from: " + oldFilter + " to: " + newFilter;
                        media.filterPattern = newFilter;
                        findMediaText = "";
                        return;
                     }
                  }
                  mediaErrorMessage = "Media file: " + media.uniqueFileName + " already in product";
                  findMediaText = "";
                  return;
               }

               String extraStatus = "";
               String oldFilter = media.filterPattern;
               if (newFilter != null) {
                  if (oldFilter != null && !oldFilter.equals(newFilter)) {
                     extraStatus = " and replaced old options: " + oldFilter + " to: " + newFilter;
                  }
               }
               else if (media.filterPattern != null) {
                  extraStatus = " and cleared old options: " + oldFilter;
               }
               media.filterPattern = newFilter;

               mediaStatusMessage = "Added file: " + media.uniqueFileName + " to media" + extraStatus;
               prodMedia.add(media);
               if (setList) {
                  product.altMedia = prodMedia;
                  product.mainMedia = media;
               }
               findMediaText = "";
               return;
            }
         }
      }
      // Restore the old text
      mediaErrorMessage = "No media found with fileName: " + uniqueFileName;
   }

   void removeProductMedia(long mediaId) {
      clearMediaErrors();
      if (product == null || product.altMedia == null)
         mediaErrorMessage = "No product or media for remove";
      else {
         for (int i = 0; i < product.altMedia.size(); i++) {
            if (product.altMedia.get(i).id == mediaId) {
               product.altMedia.remove(i);
               return;
            }
         }
         mediaErrorMessage = "Remove product media not found";
      }
   }

   void updateLongDesc(String htmlText) {
      if (product == null)
         return;
      String error = HTMLElement.validateClientHTML(htmlText, HTMLElement.formattingTags, HTMLElement.formattingAtts);
      if (error == null)
         product.longDesc = htmlText;
      else // TODO: fix this and log it as a security warning
         System.err.println("Invalid html text submission: " + htmlText + ": " + error);
   }

   void addMediaResult(Object res) {
      if (res instanceof Object[])
         res = Arrays.asList((Object[]) res);
      List resList = (List) res;
      clearMediaErrors();
      if (resList.size() == 1) {
         String nameWithRev = (String) resList.get(0);
         // Here we want to find all versions of the file uploaded just for context
         List<ManagedMedia> mediaRes = MediaManagerView.searchForText(nameWithRev);
         int selIx = -1;
         ManagedMedia newMedia = null;
         for (int i = 0; i < mediaRes.size(); i++) {
            if (mediaRes.get(i).uniqueFileName.equals(nameWithRev)) {
               newMedia = mediaRes.get(i);
               selIx = i;
               break;
            }
         }
         if (newMedia != null) {
            String newFilter = getMediaFilterPattern();
            if (newFilter != null) {
               String oldFilter = newMedia.filterPattern;
               if (oldFilter != null && !oldFilter.equals(newFilter)) {
                  System.out.println("Changing filter pattern from: " + oldFilter + " to: " + newFilter);
               }
               newMedia.filterPattern = newFilter;
            }

            List<ManagedMedia> altMedia = product.altMedia;
            boolean setList = false;
            if (altMedia == null) {
               altMedia = new ArrayList<ManagedMedia>();
               setList = true;
            }
            if (altMedia.contains(newMedia)) {
               // The result may have already been uploaded and added to the product
               mediaErrorMessage = "Media already exists in product: " + nameWithRev;
               return;
            }
            else
               altMedia.add(newMedia);
            if (setList)
               product.altMedia = altMedia;
            if (product.mainMedia == null)
               product.mainMedia = newMedia;

            mediaStatusMessage = "Added media file: " + newMedia.uniqueFileName + " " + newMedia.fileType + " " + newMedia.width + "x" + newMedia.height + (newFilter == null ? "" : " for options: " + newFilter);
         }
         else
            mediaErrorMessage = "No media: " + nameWithRev;
      }
      else {
         System.err.println("*** Multiple files returned for add product media - expecting only one");
      }
   }

   void addMediaError(String err) {
      mediaStatusMessage = null;
      mediaErrorMessage = err;
   }

   // Options view

   void startNewOptionScheme() {
      optionScheme = (OptionScheme) OptionScheme.getDBTypeDescriptor().createInstance();
      optionScheme.schemeName = product.pathName;

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

      List<OptionScheme> schemes = OptionScheme.findBySchemeName(optionSchemeName);
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
}

