import java.util.Arrays;

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
      productList = searchForText(searchText);
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
      statusMessage = null;
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

   void startAddProduct() {
      if (addInProgress)
         return;

      Product newProd = (Product) Product.getDBTypeDescriptor().createInstance();
      if (product != null) {
         newProd.options = product.options;
         newProd.sku = product.sku;
         newProd.name = "copy of " + product.name;
         newProd.pathName = product.pathName;
         newProd.mainMedia = product.mainMedia;
      }
      else {
         newProd.name = "";
         newProd.pathName = "";
      }
      product = newProd;

      // Automatically set this based on the product name unless it's already specified
      autoUpdatePath = product.pathName == null || product.pathName.length() == 0;

      addInProgress = true;
   }

   void startAddSku() {
      if (addSkuInProgress)
         return;
      sku = (Sku) Sku.getDBTypeDescriptor().createInstance();
      sku.skuCode = "";
      addSkuInProgress = true;
   }

   void completeAddSku() {
      if (!addSkuInProgress)
         return;
      skuErrorMessage = null;

      try {
         sku.dbInsert(false);
         product.sku = sku;
         addSkuInProgress = false;
      }
      catch (IllegalArgumentException exc) {
         skuErrorMessage = "System error: " + exc;
      }
   }

   void doAddProduct() {
      if (product.propErrors != null && product.propErrors.size() > 0)
         errorMessage = "Errors in new product";
      else
         errorMessage = null;
      try {
         if (addInProgress) {
            product.dbInsert(false);

            // This after we have inserted it to prevent the category from trying to insert the product due to the
            // bi-directional parentCategory/products relationships
            if (defaultCategory != null)
               product.parentCategory = defaultCategory;

            ArrayList<Product> newList = new ArrayList<Product>();
            newList.add(product);
            productList = newList;
            product = null;
            addInProgress = false;
            statusMessage = "Product created";
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
      product = null;
      addInProgress = false;
   }

   void updateMatchingSkus(String pattern) {
      if (pattern == null || pattern.length() < 2)
         return;
      matchingSkus = (List<Sku>) Sku.getDBTypeDescriptor().searchQuery(null, pattern, searchOrderBy, 0, 5);
   }

   void updateProductSku(String skuCode) {
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
      matchingCategories = (List<Category>) Category.getDBTypeDescriptor().searchQuery(null, pattern, searchOrderBy, 0, 5);
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
}
