class ProductInventory implements IPropValidator {
   int quantity;
   Date nextAvail;

   @Bindable
   @DBPropertySettings(persist=false)
   Map<String,String> propErrors = null;

   void updateQuantityStr(String qstr) {
      try {
         quantity = Integer.parseInt(qstr);
         removePropError("quantity");
      }
      catch (NumberFormatException exc) {
         addPropError("quantity", "Not an integer");
      }
   }
}