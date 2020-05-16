@DBTypeSettings(typeId=2)
class PhysicalSku extends Sku {
   ProductInventory inventory;
   BigDecimal weight, height, width, length;

   public boolean getInStock() {
      return inventory == null || inventory.quantity > 0;
   }
}
