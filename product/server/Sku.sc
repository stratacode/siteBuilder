Sku {
   Sku createSkuWithOptions(List<OptionValue> skuOptions, boolean doInsert) {
      Sku optionSku = createOptionSku();
      optionSku.skuCode = getSkuOptionCode(skuOptions, skuCode);
      optionSku.options = skuOptions;
      if (doInsert)
         optionSku.dbInsert(false);

      return optionSku;
   }

   String getSkuOptionCode(List<OptionValue> optionList, String mainSkuCode) {
      StringBuilder newSkuCode = new StringBuilder();
      newSkuCode.append(mainSkuCode);
      int numOptions = optionList.size();
      for (int i = 0; i < numOptions; i++) {
         newSkuCode.append("-");
         newSkuCode.append(optionList.get(i).skuSymbol);
      }
      return newSkuCode.toString();
   }

   List<Sku> initSkuOptions() {
      if (skuOptions != null)
         return skuOptions;

      OptionScheme prodOpts = optionScheme;
      if (prodOpts == null)
         return null;

      ArrayList<Sku> newSkus = new ArrayList<Sku>();

      ArrayList<OptionValue> emptyList = new ArrayList<OptionValue>();

      addSkusWithOptions(newSkus, emptyList, 0, prodOpts.options);
      skuOptions = newSkus;
      return newSkus;
   }

   private void addSkusWithOptions(List<Sku> results, List<OptionValue> parentOpts, int curOptIx, List<ProductOption> options) {
      int numOptions = options.size();
      ProductOption curOpt = options.get(curOptIx);
      for (OptionValue optValue:curOpt.optionValues) {
         ArrayList<OptionValue> next = new ArrayList<OptionValue>(parentOpts);
         next.add(optValue);

         if (curOptIx == numOptions - 1) {
            results.add(createSkuWithOptions(next, true));
         }
         else {
            addSkusWithOptions(results, next, curOptIx + 1, options);
         }
      }
   }

   void verifySkuOptions(List<Sku> validSkuOptions, List<Sku> missingSkuOptions, List<Sku> invalidSkuOptions) {
      OptionScheme scheme = optionScheme;
      if (scheme == null) {
         if (skuOptions != null) {
            invalidSkuOptions.addAll(skuOptions);
         }
         return;
      }

      ArrayList<OptionValue> emptyList = new ArrayList<OptionValue>();

      verifySkusWithOptions(validSkuOptions, missingSkuOptions, invalidSkuOptions, emptyList, 0, scheme.options);

      if (skuOptions != null) {
         for (Sku sku:skuOptions) {
            if (!validSkuOptions.contains(sku))
               invalidSkuOptions.add(sku);
         }
      }
   }

   private void verifySkusWithOptions(List<Sku> validSkuOptions, List<Sku> missingSkuOptions, List<Sku> invalidSkuOptions,
                                      List<OptionValue> parentOpts, int curOptIx, List<ProductOption> options) {
      int numOptions = options.size();
      ProductOption curOpt = options.get(curOptIx);
      for (OptionValue optValue:curOpt.optionValues) {
         ArrayList<OptionValue> next = new ArrayList<OptionValue>(parentOpts);
         next.add(optValue);

         if (curOptIx == numOptions - 1) {
            Sku matchingSku = findSkuForOptions(skuOptions, next);
            if (matchingSku == null)
               missingSkuOptions.add(createSkuWithOptions(next, false));
            else
               validSkuOptions.add(matchingSku);
         }
         else {
            verifySkusWithOptions(validSkuOptions, missingSkuOptions, invalidSkuOptions, next, curOptIx + 1, options);
         }
      }
   }

   void addSkuOption(Sku skuOption) {
      if (skuOptions == null)
         skuOptions = new ArrayList<Sku>();
      skuOptions.add(skuOption);
   }
}