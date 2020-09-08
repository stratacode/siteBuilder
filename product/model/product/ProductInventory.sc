import sc.bind.IChangeable;

@DBTypeSettings(versionProp="version")
class ProductInventory implements IPropValidator, IChangeable {
   int quantity;
   Date nextAvail;

   long version;

   @Bindable
   @DBPropertySettings(persist=false)
   Map<String,String> propErrors = null;

   void updateQuantityStr(String qstr) {
      try {
         quantity = Integer.parseInt(qstr);
         removePropError("quantity");
         Bind.sendChangedEvent(this, null);
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
      return TextUtil.formatUserDate(today, false);
   }

   void updateNextAvailStr(String value) {
      try {
         nextAvail = DynUtil.parseDate(value);
         Bind.sendChangedEvent(this, null);
      }
      catch (IllegalArgumentException exc) {
         addPropError("nextAvail", "Invalid date format");
      }
   }

   String getNextAvailDisplayStr() {
      if (nextAvail != null) {
         return "Available on: " + TextUtil.formatUserDate(nextAvail, false);
      }
      return "";
   }
}