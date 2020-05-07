@DBTypeSettings(typeId=2)
class PhysicalSku extends Sku {
   ProductInventory inventory;
   BigDecimal weight, height, width, length;
}
