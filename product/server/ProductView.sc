ProductView {
   boolean optionsValid = false;

   void init() {
      validateProduct();
      currencySymbol = store.defaultCurrency.symbol;
   }

   void validateProduct() {
      if (pathName == null)
         return;

      if (product == null || !product.pathName.equals(pathName)) {
         optionsValid = false;
         List<Product> prods = Product.findByPathName(pathName, store, 0, 1);
         if (prods.size() > 0) {
            product = prods.get(0);
            currentQuantity = product.defaultQuantity;
            altMediaIndex = 0;
            validateCurrentMedia();

            productViewError = null;
         }
         else {
            productViewError = "No product found for: " + pathName;
         }
      }

      validateCatalogElement();
      if (product != null) {
         available = product.available;
      }
      if (available) {
         if (!(currentSku instanceof PhysicalSku))
            inStock = true;
         else
            inStock = currentSku.inStock;
      }
      else
         inStock = false;

      initOptions();
   }

   void initOptions() {
      if (product == null)
         return;
      options = product.options;
      ProductOptions productOptions = options;
      if (productOptions != null) {
         List<Sku> skuOptions = product.skuOptions;
         Sku defaultSku = null;
         if (skuOptions != null) {
            for (Sku optSku:skuOptions) {
               if (optSku.inStock) {
                  defaultSku = optSku;
                  break;
               }
            }
         }

         List<OptionValue> defaultOptions = defaultSku != null ? defaultSku.options : options.defaultOptions;

         int numOptions = options.options.size();
         if (defaultSku == null) {
            defaultSku = createOptionSku(defaultOptions);
         }

         currentSku = defaultSku;

         if (numOptions != defaultOptions.size()) {
            System.err.println("*** Sku options out of sync");
            numOptions = Math.min(defaultOptions.size(), numOptions);
         }

         optionViews = new ArrayList<OptionView>();
         for (int i = 0; i < numOptions; i++) {
            ProductOption opt = options.options.get(i);
            OptionView optionView = new OptionView();
            optionView.productView = this;
            optionView.option = opt;
            optionView.enabled = new ArrayList<Boolean>();
            OptionValue defaultValue = defaultOptions.get(i);
            int selIndex = -1;
            int ix = 0;
            for (OptionValue optValue:opt.optionValues) {
               boolean enabled;
               if (product.skuOptions != null) {
                  Sku optionSku = product.getSkuForOptionsWith(defaultOptions, i, optValue);
                  enabled = optionSku != null && optionSku.inStock;
               }
               else
                  enabled = true;
               optionView.enabled.add(enabled);
               if (optValue.name.equals(defaultValue.name))
                  selIndex = ix;
               ix++;
            }
            optionView.selectedIndex = selIndex == -1 ? 0 : selIndex;
            optionViews.add(optionView);
         }
      }
      else {
         productOptions = null;
         currentSku = product.sku;
         optionViews = new ArrayList<OptionView>();
      }
      optionsValid = true;
   }

   Sku createOptionSku(List<OptionValue> skuOptions) {
      Sku mainSku = product.sku;
      Sku optionSku = mainSku.createOptionSku();
      StringBuilder skuCode = new StringBuilder();
      skuCode.append(mainSku.skuCode);
      int numOptions = options.options.size();
      for (int i = 0; i < numOptions; i++) {
         skuCode.append("-");
         skuCode.append(skuOptions.get(i).skuPart);
      }
      optionSku.skuCode = skuCode.toString();
      optionSku.options = skuOptions;
      optionSku.dbInsert(false);

      return optionSku;
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

   void validateOptions() {
      if (optionsValid)
         return;
      if (options != product.options) {
         initOptions();
      }
      else {
         int numOptions = options.options.size();
         if (optionViews.size() != numOptions) {
            System.err.println("*** Mismatching number of options");
            return;
         }

         for (int i = 0; i < numOptions; i++) {
            ProductOption opt = options.options.get(i);
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
               if (product.skuOptions != null) {
                  Sku optionSku = product.getSkuForOptionsWith(selectedOptions, j, optValue);
                  enabled = optionSku != null && optionSku.inStock;
               }
               optionView.enabled.set(j, enabled);
            }
         }

         List<OptionValue> selectedOptions = getSelectedOptions();
         if (product.skuOptions != null)
            currentSku = product.getSkuForOptionsWith(selectedOptions, -1, null);
         // No sku exists for this collection of options so creating it here - ideally this would be part of the
         // management UI since some combination of these options might not exist or for inventory
         else
            currentSku = createOptionSku(selectedOptions);
      }
      optionsValid = true;
   }

   void addToCart() {
      OrderView orderView = currentOrderView;
      orderView.addLineItem(product, currentSku, product.skuParts, currentQuantity);
   }

   void startProductSync() {
      if (product != null) {
         SyncContext syncCtx = SyncManager.getSyncContextForInst(product);
         if (syncCtx == null) {
            SyncManager.addSyncInst(product, false, false, "appSession", null);
         }
         SyncManager.startSync(product, "products");
         SyncManager.startSync(product, "subCategories");
         SyncManager.startSync(product, "sku");
         SyncManager.startSync(product, "skuParts");
         SyncManager.startSync(product, "skuOptions");
      }
   }
}
