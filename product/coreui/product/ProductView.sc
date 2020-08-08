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

   boolean mediaIsAvailable(ManagedMedia media) {
      if (!super.mediaIsAvailable(media))
         return false;
      String filterPattern = media.filterPattern;
      if (filterPattern == null || optionScheme == null)
         return true;

      String next = filterPattern;
      List<OptionValue> selOpts = getSelectedOptions();
      do {
         int rix = next.indexOf(",");
         String optStr = rix == -1 ? next : next.substring(0, rix);
         int eix = optStr.indexOf("=");
         if (eix == -1) {
            System.err.println("*** Invalid filterPattern for media: " + media.uniqueFileName);
            return true;
         }
         String optName = optStr.substring(0, eix);
         if (optStr.length() <= eix + 1) {
            System.err.println("*** Invalid filterPattern for media: " + media.uniqueFileName);
            return true;
         }
         String optVal = optStr.substring(eix+1);
         boolean found = false;
         for (int i = 0; i < selOpts.size(); i++) {
            if (optionScheme.options.get(i).optionName.equals(optName)) {
               if (!selOpts.get(i).optionValue.equals(optVal))
                  return false;
               found = true;
               break;
            }
         }
         if (!found) {
            System.err.println("*** Option: " + optStr + " in filterPattern for media: " + media.uniqueFileName + " not found in optionScheme: " + optionScheme);
         }
         if (rix == -1 || next.length() <= rix+1)
            break;
         next = next.substring(rix+1);
      } while(true);

      return true;
   }

}
