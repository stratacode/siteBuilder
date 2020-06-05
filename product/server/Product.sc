Product {
   List<Sku> initSkuOptions() {
      if (skuOptions != null)
         return skuOptions;

      ProductOptions prodOpts = options;
      if (prodOpts == null)
         return null;

      ArrayList<Sku> newSkus = new ArrayList<Sku>();

      ArrayList<OptionValue> emptyList = new ArrayList<OptionValue>();

      addSkusWithOptions(newSkus, emptyList, 0, prodOpts.options);
      skuOptions = newSkus;
      return newSkus;
   }

   Sku createOptionSku(List<OptionValue> skuOptions) {
      Sku mainSku = sku;
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


   private void addSkusWithOptions(List<Sku> results, List<OptionValue> parentOpts, int curOptIx, List<ProductOption> options) {
      int numOptions = options.size();
      ProductOption curOpt = options.get(curOptIx);
      for (OptionValue optValue:curOpt.optionValues) {
         ArrayList<OptionValue> next = new ArrayList<OptionValue>(parentOpts);
         next.add(optValue);

         if (curOptIx == numOptions - 1) {
            results.add(createOptionSku(next));
         }
         else {
            addSkusWithOptions(results, next, curOptIx + 1, options);
         }
      }
   }
}
