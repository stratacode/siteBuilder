import java.util.Arrays;
import java.util.TreeSet;

import sc.lang.html.HTMLElement;

ProductManagerView {
   store =: storeChanged();

   void storeChanged() {
      resetForm();
   }

   static final List<String> searchOrderBy = Arrays.asList("-lastModified");

   static List<Product> searchForText(String text) {
      return (List<Product>) Product.getDBTypeDescriptor().searchQuery(null, text, searchOrderBy, -1, -1);
   }

   void doSearch() {
      String txt = searchText == null ? "" : searchText;
      productList = searchForText(txt);
   }

   void doSelectProduct(Product toSel) {
      // We might have just removed this product so don't make it current again
      if (((sc.db.DBObject)toSel.getDBObject()).isActive()) {
         if (toSel == product)
            product = null;
         else
            product = toSel;
      }
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
      List<LineItem> refsToProd = (List<LineItem>) LineItem.getDBTypeDescriptor().findBy(propValues, null, Arrays.asList("product"), null, 0, 1);
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
   }

   void startAddSku() {
      if (addSkuInProgress)
         return;
      initTemporarySku();
      addSkuInProgress = true;
   }

   void initTemporarySku() {
      sku = (Sku) Sku.getDBTypeDescriptor().createInstanceOfType(skuTypeId);
      sku.skuCode = "";
      if (sku instanceof PhysicalSku)
         psku = (PhysicalSku) sku;
      else
         psku = null;
   }

   void completeAddSku() {
      if (!addSkuInProgress)
         return;
      skuErrorMessage = null;

      try {
         sku.validateSku();
         if (sku.propErrors == null) {
            sku.dbInsert(false);
            product.sku = sku;
            addSkuInProgress = false;
         }
         else
            System.err.println("*** property errors for sku: " + sku.propErrors);
      }
      catch (IllegalArgumentException exc) {
         skuErrorMessage = "System error: " + exc;
      }
   }

   void doAddProduct() {
      clearFormErrors();
      if (product.propErrors != null && product.propErrors.size() > 0)
         errorMessage = "Errors in new product";
      else {
         errorMessage = null;
         try {
            if (addInProgress) {
               product.dbInsert(false);

               String newName = product.pathName;

               // This after we have inserted it to prevent the category from trying to insert the product due to the
               // bi-directional parentCategory/products relationships
               if (defaultCategory != null)
                  product.parentCategory = defaultCategory;

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
   }

   void cancelAddProduct() {
      addInProgress = false;
      resetForm();
   }

   void updateSkuType(int typeId) {
      if (typeId == skuTypeId)
         return;
      skuTypeId = typeId;

      String saveCode = null; // TODO: add other sku attributes to save across a type change
      if (sku != null) {
         saveCode = sku.skuCode;
      }
      initTemporarySku();
      if (saveCode != null && saveCode.length() > 0)
         sku.skuCode = saveCode;
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
      List<Sku> allMatches = (List<Sku>) Sku.getDBTypeDescriptor().searchQuery(null, pattern, searchOrderBy, 0, 20);
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
      skuErrorMessage = null;

      if (skuCode == null || skuCode.trim().length() == 0) {
         product.sku = null;
         return;
      }

      List<Sku> skus = Sku.findBySkuCode(skuCode);
      if (skus != null) {
         if (skus.size() == 1) {
            product.sku = skus.get(0);
            return;
         }
         else if (skus.size() > 1) {
            product.sku = skus.get(0);
            //skuErrorMessage = "Warning multiple skus with skuCode: " + skuCode;
            return;
         }
      }
      skuErrorMessage = "No sku with skuCode: " + skuCode;
   }

   void updateMatchingCategories(String pattern) {
      if (pattern == null || pattern.length() < 2)
         return;
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<Category> res = new ArrayList<Category>();
      List<Category> allMatches = (List<Category>) Category.getDBTypeDescriptor().searchQuery(null, pattern, searchOrderBy, 0, 20);
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
      List<Category> cats = Category.findByPathName(pathName, store, 0, 10);
      boolean newProduct = product.getDBObject().isTransient();
      if (cats != null) {
         if (cats.size() == 1) {
            if (!newProduct)
               product.parentCategory = cats.get(0);
            product.removePropError("parentCategory");
            defaultCategory = cats.get(0);
            return;
         }
         else if (cats.size() > 1) {
            if (!newProduct)
               product.parentCategory = cats.get(0);
            product.removePropError("parentCategory");
            defaultCategory = cats.get(0);
            //product.addPropError("parentCategory", "Warning multiple categories with pathName: " + pathName);
            return;
         }
      }
      product.addPropError("parentCategory", "No category with path name: " + pathName);
   }

   void updateMatchingMedia(String pattern) {
      if (pattern == null || pattern.length() < 2)
         return;
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<ManagedMedia> res = new ArrayList<ManagedMedia>();
      List<ManagedMedia> allMatches = (List<ManagedMedia>) ManagedMedia.getDBTypeDescriptor().searchQuery(null, pattern, searchOrderBy, 0, 20);
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
                  mediaErrorMessage = "Media file: " + media.uniqueFileName + " already in product";
                  return;
               }
               mediaStatusMessage = "Added file: " + media.uniqueFileName + " to media";
               prodMedia.add(media);
               if (setList) {
                  product.altMedia = prodMedia;
                  product.mainMedia = media;
               }
               return;
            }
         }
      }
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
      String error = HTMLElement.validateClientHTML(htmlText, HTMLElement.formattingTags);
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
            mediaStatusMessage = "Added media file: " + newMedia.uniqueFileName + " " + newMedia.fileType + " " + newMedia.width + "x" + newMedia.height;
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

      optionScheme.options = new ArrayList<ProductOption>();
      addNewOption();

      showNewOptionsView = true;
   }

   void updateMatchingOptionSchemes(String pattern) {
      if (pattern == null)
         pattern = "";
      List<OptionScheme> allMatches = (List<OptionScheme>) OptionScheme.getDBTypeDescriptor().searchQuery(null, pattern, searchOrderBy, 0, 20);
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
      List<OptionScheme> schemes = OptionScheme.findBySchemeName(optionSchemeName);
      if (schemes != null) {
         if (schemes.size() == 1) {
            sku.optionScheme = schemes.get(0);
            return;
         }
         else if (schemes.size() > 1) {
            sku.optionScheme = schemes.get(0);
            //skuErrorMessage = "Warning multiple skus with skuCode: " + skuCode;
            return;
         }
      }
      optionStatusMessage = null;
      optionErrorMessage = "No option scheme with scheme name: " + optionSchemeName;
   }

   void updateHasOptions(boolean status) {
      if (sku == null)
         return;
      if (!status) {
         sku.optionScheme = null;
      }
      showOptionsView = status;
   }

   void addNewOption() {
      ProductOption opt = (ProductOption) ProductOption.getDBTypeDescriptor().createInstance();
      opt.optionName = "";
      opt.optionValues = new ArrayList<OptionValue>();
      OptionValue optVal = new OptionValue();
      optVal.optionValue = "";
      optVal.skuSymbol = "";
      opt.optionValues.add(optVal);
      opt.defaultValue = optVal;
      optionScheme.options.add(opt);
   }

   void removeOption(ProductOption option) {
      if (!optionScheme.options.remove(option))
         System.err.println("*** Error - unable to find option to remove");
   }

   void addNewOptionValue(ProductOption option) {
      OptionValue optVal = new OptionValue();
      optVal.optionValue = "";
      optVal.skuSymbol = "";
      option.optionValues.add(optVal);
   }

   void removeOptionValue(ProductOption option, OptionValue optVal) {
      boolean isDefault = option.defaultValue == optVal;
      if (!option.optionValues.remove(optVal))
         System.err.println("*** Error - no option value found to remove");
      else if (isDefault && option.optionValues.size() > 0)
         option.defaultValue = option.optionValues.get(0);
   }

   void completeNewOptionScheme() {
      optionScheme.dbInsert(false);
      sku.optionScheme = optionScheme;
      showNewOptionsView = false;
      optionStatusMessage = "Option scheme: " + optionScheme.schemeName + " added";
   }

   void cancelNewOptionScheme() {
      optionScheme = null;
      showNewOptionsView = false;
   }

}
