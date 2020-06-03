class ProductView extends CatalogElementView {
   @Sync(initDefault=true)
   Product product;

   @Sync
   String currencySymbol;

   pathName =: validateProduct();

   @Sync(initDefault=true)
   Sku currentSku;

   @Sync
   boolean available;
   @Sync
   boolean inStock;

   @Sync
   String productViewError;

   @Sync(initDefault=true)
   ProductOptions options;
   @Sync(initDefault=true)
   List<OptionView> optionViews;

   object quantityConverter extends IntConverter {
   }

   String currentQuantityStr :=: quantityConverter.intToString(currentQuantity);
   @Sync(initDefault=true)
   int currentQuantity;

   CatalogElement getCatalogElement() {
      return product;
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


}
