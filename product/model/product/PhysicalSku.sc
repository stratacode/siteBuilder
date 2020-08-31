@DBTypeSettings(typeId=2)
class PhysicalSku extends Sku {
   ProductInventory inventory;
   BigDecimal weight, height, width, length;

   inventory =: Bind.sendChangedEvent(this, "displaySummary");

   PhysicalSku createOptionSku() {
      PhysicalSku res = new PhysicalSku();
      copyInto(res);
      return res;
   }

   void copyInto(PhysicalSku other) {
      super.copyInto(other);
      other.weight = weight;
      other.height = height;
      other.length = length;
      if (inventory != null) {
         other.inventory = new ProductInventory();
         other.inventory.quantity = inventory.quantity;
         other.inventory.nextAvail = inventory.nextAvail;
      }
   }

   boolean getInStock() {
      return available && (inventory == null || inventory.quantity > 0);
   }

   String getInventoryDisplayStr() {
      String quantStr = "";
      String nextAvailStr = "";
      if (inventory != null) {
         if (inventory.quantity > 0)
            quantStr = "(" + String.valueOf(inventory.quantity) + ")";
         if (inventory.nextAvail != null)
            nextAvailStr = " next avail: " + DynUtil.formatDate(inventory.nextAvail);
      }
      return (inStock ? " in stock" : (available ? " out of stock" : "discontinued")) + nextAvailStr + quantStr;
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
