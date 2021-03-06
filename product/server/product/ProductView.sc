ProductView {
   @Sync(syncMode=SyncMode.Disabled)
   boolean optionsValid = false;

   int orderChangeCt := storeView.orderView.orderChangeCt;
   orderChangeCt =: validateInStock();

   // Here so we only run it once and sync the results
   pathName =: validateProductPath();

   pageVisitCount =: validateView();

   @Sync(syncMode=SyncMode.Disabled)
   ProductInventory inventory := currentSku.inventory;

   ProductInventory prodInventory := product.sku.inventory;
   prodInventory =: validateInStock();

   void init() {
      if (storeView.store == null)
         return;
      validateProductPath();
      currencySymbol = storeView.store.defaultCurrency.symbol;
   }

   void validateView() {
      optionsValid = false;
      validateOptions();
      validateInStock();
   }

   void validateProductPath() {
      if (pathName == null)
         return;

      if (product == null || !product.pathName.equals(pathName)) {
         optionsValid = false;
         List<Product> prods = Product.findByPathName(pathName, storeView.store, 0, 1);
         if (prods.size() > 0) {
            product = prods.get(0);

            if (PTypeUtil.testMode)
               DBUtil.addTestIdInstance(product, "product/" + pathName);

            initOptions();
            altMediaIndex = 0;
            mediaChanged();

            productViewError = null;

            UserSession us = storeView.getUserSession();
            if (us != null)
               us.addProductEvent(product);
         }
         else {
            productViewError = "No product found for: " + pathName;
         }
      }

      validateManagedElement();
      validateInStock();
   }

   void validateInStock() {
      if (product != null) {
         available = product.available;
      }
      OrderView orderView = storeView.orderView;
      Order cart = orderView.order;
      if (cart != null)
         numInCart = cart.getReservedInventory(currentSku);
      else
         numInCart = 0;

      if (available) {
         if (!(currentSku instanceof PhysicalSku)) {
            if (currentSku == null)
               inStock = false;
            else
               inStock = true;
         }
         else {
            inStock = currentSku.inStock;
            if (inStock) {
               PhysicalSku psku = (PhysicalSku)currentSku;
               if (cart != null) {
                  inStock = psku.inventory.quantity >= (numInCart + currentQuantity);
               }
            }
         }
      }
      else {
         inStock = false;
         numInCart = 0;
      }

      validateOptionEnabled();
   }

   void initOptions() {
      if (product == null)
         return;
      OptionScheme scheme = optionScheme;
      if (scheme != null) {
         List<Sku> skuOptions = product.sku.skuOptions;

         if (skuOptions == null) {
            skuOptions = product.sku.initSkuOptions();
         }

         Sku defaultSku = Sku.findSkuForOptions(skuOptions, scheme.defaultOptions);
         if (defaultSku == null)
            System.err.println("*** Missing sku for default option combination: " + product + ": " + scheme.defaultOptions);
         if (defaultSku == null || !defaultSku.inStock) {
            if (skuOptions != null) {
               for (Sku optSku:skuOptions) {
                  if (optSku.inStock) {
                     defaultSku = optSku;
                     break;
                  }
               }
            }
         }

         if (defaultSku == null) {
            System.err.println("*** No defaultSku found for: " + product);
            return;
         }

         List<OptionValue> defaultOptions = defaultSku != null ? defaultSku.options : scheme.defaultOptions;

         int numOptions = scheme.options.size();

         currentSku = defaultSku;

         if (numOptions != defaultOptions.size()) {
            System.err.println("*** Sku options out of sync");
            numOptions = Math.min(defaultOptions.size(), numOptions);
         }

         optionViews = new ArrayList<OptionView>();
         for (int i = 0; i < numOptions; i++) {
            ProductOption opt = scheme.options.get(i);

            if (PTypeUtil.testMode)
               DBUtil.addTestIdInstance(opt, "prodOpt-" + opt.optionName);

            OptionView optionView = new OptionView();
            optionView.productView = this;
            optionView.option = opt;
            optionView.enabled = new ArrayList<Boolean>();
            OptionValue defaultValue = defaultOptions.get(i);
            int selIndex = -1;
            int ix = 0;
            for (OptionValue optValue:opt.optionValues) {
               boolean enabled;

               if (PTypeUtil.testMode)
                  DBUtil.addTestIdInstance(optValue, "optVal-" + optValue.optionValue);

               if (skuOptions != null) {
                  Sku optionSku = product.sku.getSkuForOptionsWith(defaultOptions, i, optValue);
                  enabled = optionSku != null && optionSku.inStock;
               }
               else {
                  enabled = true;
               }
               optionView.enabled.add(enabled);
               if (optValue.optionValue.equals(defaultValue.optionValue))
                  selIndex = ix;
               ix++;
            }
            optionView.selectedIndex = selIndex == -1 ? 0 : selIndex;
            optionViews.add(optionView);
         }
      }
      else {
         scheme = null;
         currentSku = product.sku;
         optionViews = new ArrayList<OptionView>();
      }
      optionsValid = true;
   }

   List<OptionValue> replaceOneOption(List<OptionValue> values, int ix, OptionValue repl) {
      ArrayList<OptionValue> res = new ArrayList<OptionValue>(values);
      res.set(ix, repl);
      return res;
   }

   void invalidateOptions() {
      if (optionsValid) {
         optionsValid = false;
            DynUtil.invokeLater(new Runnable() {
               void run() {
                  validateOptions();
               }
            }, 0);
      }
   }

   void addToCart() {
      storeView.orderView.addLineItem(product, currentSku, currentQuantity);
      UserSession us = storeView.getUserSession();
      if (us != null)
         us.addAddToCartEvent(product);
      validateInStock();
   }

   void validateOptions() {
      if (optionsValid || optionScheme == null)
         return;
      if (optionScheme != product.sku.optionScheme) {
         initOptions();
      }
      else {
         int numOptions = optionScheme.options.size();
         if (optionViews.size() != numOptions) {
            System.err.println("*** Mismatching number of options");
            return;
         }

         validateOptionEnabled();

         List<OptionValue> selectedOptions = getSelectedOptions();
         if (product.sku.skuOptions != null) {
            currentSku = product.sku.getSkuForOptionsWith(selectedOptions, -1, null);
            validateInStock();
         }
         else
            System.err.println("*** No sku found for selected options in product: " + product);
      }
      optionsValid = true;
      mediaChanged();
   }

   void validateOptionEnabled() {
      if (optionViews == null || optionScheme == null)
         return;
      boolean optionsChanged = false;
      int numOptions = optionScheme.options.size();
      for (int i = 0; i < numOptions; i++) {
         ProductOption opt = optionScheme.options.get(i);
         OptionView optionView = optionViews.get(i);
         if (optionView.option != opt) {
            System.err.println("*** Mismatching option");
            return;
         }
         List<OptionValue> optValues = optionView.option.optionValues;
         int selIndex = optionView.getSelectedIndex();
         for (int j = 0; j < optValues.size(); j++) {
            OptionValue optValue = optValues.get(j);
            boolean enabled = true;;
            if (product.sku.skuOptions != null) {
               Sku optionSku = product.sku.getSkuForOptionsWith(selectedOptions, i, optValue);
               enabled = optionSku != null && optionSku.inStock;
            }
            if (enabled != optionView.enabled.get(j)) {
               optionView.enabled.set(j, enabled);
               optionsChanged = true;
            }
         }
      }
      if (optionsChanged)
         optionsChangedCt++;
   }

   void startProductSync() {
      if (product != null) {
         SyncContext syncCtx = SyncManager.getSyncContextForInst(product);
         if (syncCtx == null) {
            SyncManager.addSyncInst(product, false, false, false, "appSession", null);
         }
         SyncManager.startSync(product, "subCategories");
         SyncManager.startSync(product, "sku");
         SyncManager.startSync(product, "skuParts");
         SyncManager.startSync(product, "skuOptions");
      }
   }

}
