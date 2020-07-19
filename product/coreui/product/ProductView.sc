class ProductView extends CatalogElementView {
   @Sync(initDefault=true)
   Product product;

   product =: validateProduct();

   @Sync
   String currencySymbol;


   @Sync(initDefault=true)
   Sku currentSku;

   @Sync
   boolean available;
   @Sync
   boolean inStock;

   @Sync
   String productViewError;

   @Sync(initDefault=true)
   OptionScheme optionScheme;
   @Sync(initDefault=true)
   List<OptionView> optionViews;

   // This method runs on both client and server so the inputs are synchronized, but not the outputs
   void validateProduct() {
      validateCurrentMedia();
   }

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
