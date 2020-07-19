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

   String toString() {
      return quantity + " available" + (nextAvail != null ? " - next available: " + nextAvail : "");
   }

   static String getMinNextAvailDateStr() {
      Date today = new Date();
      return (today.getMonth() + 1) + "/" + today.getDate() + "/" + (today.getYear() + 1900);
   }

   void updateNextAvailStr(String value) {
      try {
         nextAvail = DynUtil.parseDate(value);
      }
      catch (IllegalArgumentException exc) {
         addPropError("nextAvail", "Invalid date format");
      }
   }
}