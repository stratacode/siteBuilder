@CompilerSettings(liveDynamicTypes=true)
@DBTypeSettings(typeId=1)
@Sync(onDemand=true)
@EditorSettings(displayNameProperty="skuCode")
class Sku implements IPropValidator {
   // TODO: might be convenient to have a read-only backptr to the product here?
   // It would be a reverse from multiple lists though which is not supported yet and 
   // would need to be read-only since updating it would not be able to determine which
   // list to update in the reverse side
   //Product parentProduct;

   @FindBy(orderBy="-lastModified") @DBPropertySettings(indexed=true, required=true)
   String skuCode;
   String barCode;

   BigDecimal price;
   BigDecimal discountPrice;

   Date lastModified;

   /** Set for the main sku only if it has options */
   OptionScheme optionScheme;

   /* One value for each of the options for the product */
   @Sync(initDefault=true)
   @DBPropertySettings(columnType="jsonb")
   List<OptionValue> options;

   boolean available = true;

   @Bindable
   @DBPropertySettings(persist=false)
   Map<String,String> propErrors = null;


   boolean getInStock() {
      return available;
   }

   Sku createOptionSku() {
      Sku res = new Sku();
      res.price = price;
      res.discountPrice = discountPrice;
      res.barCode = barCode;
      return res;
   }

   BigDecimal getPriceToUse() {
      return discountPrice == null ? price : discountPrice;
   }

   static Sku findSkuForOptions(List<Sku> skuOptions, List<OptionValue> opts) {
      for (Sku sku:skuOptions) {
         if (sku.options.equals(opts))
            return sku;
      }
      return null;
   }

   String toString() {
      return skuCode;
   }

   @sc.obj.EditorCreate(constructorParamNames="skuCode, price")
   static Sku createSku(String skuCode, BigDecimal price) {
      Sku newSku = new Sku();
      newSku.skuCode = skuCode;
      newSku.price = price;
      newSku.dbInsert(false);
      return newSku;
   }

   String getDisplaySummary(Storefront store) {
      BigDecimal priceToUse = getPriceToUse();
      String priceStr = priceToUse == null ? "(no price)" : " " + store.defaultCurrency.symbol + priceToUse;
      return (skuCode == null ? "" : skuCode) + priceStr;
   }

   void updatePrice(String priceStr) {
      try {
         price = new BigDecimal(priceStr);
         removePropError("price");
      }
      catch (NumberFormatException exc) {
         addPropError("price", "Invalid price");
      }
   }

   void updateDiscount(String priceStr) {
      try {
         discountPrice = new BigDecimal(priceStr);
         removePropError("discountPrice");
      }
      catch (NumberFormatException exc) {
         addPropError("discountPrice", "Invalid discountPrice");
      }
   }

   String validateSkuCode(String sc) {
      if (sc == null || sc.length() == 0)
         return "Missing sku code";
      return null;
   }

   String validatePrice(BigDecimal price) {
      if (price == null)
         return "Missing price";
      if (price.compareTo(BigDecimal.ZERO) < 0)
         return "Price is less than 0";
      return null;
   }

   void validateSku() {
      propErrors = DynUtil.validateProperties(this, null);
   }
}
