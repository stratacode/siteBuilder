@DBTypeSettings(typeId=2)
class PhysicalSku extends Sku {
   ProductInventory inventory;
   BigDecimal weight, height, width, length;

   boolean getInStock() {
      return available && (inventory == null || inventory.quantity > 0);
   }

   String getDisplaySummary(Storefront store) {
      String quantStr = "";
      String nextAvailStr = "";
      if (inventory != null) {
         if (inventory.quantity > 0)
            quantStr = String.valueOf(inventory.quantity) + " ";
         nextAvailStr = "avail: " + inventory.nextAvail != null ? DynUtil.formatDate(inventory.nextAvail) : "";
      }
      return super.getDisplaySummary(store) + quantStr + (inStock ? " in stock" : (available ? " out of stock" : "discontinued")) + nextAvailStr;
   }

   void updateWeight(String str) {
      try {
         weight = new BigDecimal(str);
         removePropError("weight");
      }
      catch (NumberFormatException exc) {
         addPropError("weight", "Invalid weight");
      }
   }

   void updateWidth(String str) {
      try {
         width = new BigDecimal(str);
         removePropError("width");
      }
      catch (NumberFormatException exc) {
         addPropError("width", "Invalid width");
      }
   }

   void updateHeight(String str) {
      try {
         height = new BigDecimal(str);
         removePropError("height");
      }
      catch (NumberFormatException exc) {
         addPropError("height", "Invalid height");
      }
   }

   void updateLength(String str) {
      try {
         length = new BigDecimal(str);
         removePropError("length");
      }
      catch (NumberFormatException exc) {
         addPropError("length", "Invalid length");
      }
   }
}
