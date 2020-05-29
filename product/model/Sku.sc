@DBTypeSettings(typeId=1)
class Sku {
   // TODO: might be convenient to have a read-only backptr to the product here?
   // It would be a reverse from multiple lists though which is not supported yet and 
   // would need to be read-only since updating it would not be able to determine which
   // list to update in the reverse side
   //Product parentProduct;

   String skuCode;
   String barCode;

   BigDecimal price;
   BigDecimal discountPrice;

   /* One value for each of the options for the product */
   List<OptionValue> options;

   boolean getInStock() {
      return true;
   }

   Sku createTempSku() {
      Sku res = new Sku();
      res.price = price;
      res.discountPrice = discountPrice;
      res.barCode = barCode;
      return res;
   }

   BigDecimal getPriceToUse() {
      return discountPrice == null ? price : discountPrice;
   }
}
