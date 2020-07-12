@DBTypeSettings(typeId=2)
class PhysicalSku extends Sku {
   ProductInventory inventory;
   BigDecimal weight, height, width, length;

   public boolean getInStock() {
      return available && (inventory == null || inventory.quantity > 0);
   }

   String getDisplaySummary(Storefront store) {
      return super.getDisplaySummary(store) + (inStock ? " in stock" : (available ? " out of stock" : "discontinued")) + (inventory == null ? "" : " " + inventory);
   }
}
