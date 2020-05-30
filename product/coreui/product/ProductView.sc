class ProductView extends CatalogElementView {
   Product product;

   String currencySymbol;

   pathName =: validateProduct();

   Sku currentSku;

   boolean available;
   boolean inStock;

   String productViewError;

   ProductOptions options;
   List<OptionView> optionViews;
   boolean optionsValid = false;

   object quantityConverter extends IntConverter {
   }

   String currentQuantityStr :=: quantityConverter.intToString(currentQuantity);
   int currentQuantity;

   void init() {
      validateProduct();
      currencySymbol = store.defaultCurrency.symbol;
   }

   CatalogElement getCatalogElement() {
      return product;
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

         // If there's a specific sku for inventory use the options from that. Otherwise, choose the defaults
         // as configured in the ProductOptions.
         List<OptionValue> defaultOptions = defaultSku != null ? defaultSku.options : options.defaultOptions;

         int numOptions = options.options.size();
         if (defaultSku == null) {
            defaultSku = createTempOptionSku(defaultOptions);
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
            optionView.option = opt;
            optionView.values = new ArrayList<OptionValueView>();
            OptionValue defaultValue = defaultOptions.get(i);
            for (OptionValue optValue:opt.optionValues) {
               OptionValueView valueView = new OptionValueView();
               valueView.productView = this;
               valueView.value = optValue;
               valueView.selected = optValue.name.equals(defaultValue.name);
               if (product.skuOptions != null) {
                  Sku optionSku = product.getSkuForOptionsWith(defaultOptions, i, optValue);
                  valueView.enabled = optionSku != null && optionSku.inStock;
               }
               else
                  valueView.enabled = true;
               optionView.values.add(valueView);
            }
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

   Sku createTempOptionSku(List<OptionValue> skuOptions) {
      Sku mainSku = product.sku;
      Sku tempSku = mainSku.createTempSku();
      StringBuilder skuCode = new StringBuilder();
      skuCode.append(mainSku.skuCode);
      int numOptions = options.options.size();
      for (int i = 0; i < numOptions; i++) {
         skuCode.append("-");
         skuCode.append(skuOptions.get(i).skuPart);
      }
      tempSku.skuCode = skuCode.toString();
      tempSku.options = skuOptions;

      return tempSku;
   }

   List<OptionValue> getSelectedOptions() {
      ArrayList<OptionValue> res = new ArrayList<OptionValue>();
      int numOptions = optionViews.size();
      for (int i = 0; i < numOptions; i++) {
         OptionView optView = optionViews.get(i);
         res.add(optView.getSelectedOption());
      }
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
            int selIndex = optionView.getSelectedIndex();
            for (int j = 0; j < optionView.values.size(); j++) {
               OptionValueView valueView = optionView.values.get(j);
               if (product.skuOptions != null) {
                  Sku optionSku = product.getSkuForOptionsWith(selectedOptions, j, valueView.value);
                  valueView.enabled = optionSku != null && optionSku.inStock;
               }
               valueView.selected = j == selIndex;
            }
         }

         List<OptionValue> selectedOptions = getSelectedOptions();
         if (product.skuOptions != null)
            currentSku = product.getSkuForOptionsWith(selectedOptions, -1, null);
         else
            currentSku = createTempOptionSku(selectedOptions);

      }
      optionsValid = true;
   }

   void addToCart() {
      OrderView orderView = currentOrderView;
      orderView.addLineItem(product, currentSku, product.skuParts, currentQuantity);
   }
}
