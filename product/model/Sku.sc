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

   @DBPropertySettings(columnType="jsonb")
   List<Sku> skuParts;

   /* Used for when there are options to hold the sku's for each available option combination */
   @DBPropertySettings(columnType="jsonb")
   List<Sku> skuOptions;

   /* Set for a skuOption only */
   @Sync(initDefault=true)
   @DBPropertySettings(columnType="jsonb")
   List<OptionValue> options;

   boolean available = true;

   @Bindable
   @DBPropertySettings(persist=false)
   Map<String,String> propErrors = null;

   @DBPropertySettings(indexed=true)
   Storefront store;

   boolean getInStock() {
      return available;
   }

   Sku createOptionSku() {
      Sku res = new Sku();
      copyInto(res);
      return res;
   }

   void copyInto(Sku other) {
      other.price = price;
      other.discountPrice = discountPrice;
      other.barCode = barCode;
      other.store = store;
   }

   BigDecimal getPriceToUse() {
      BigDecimal itemPrice = discountPrice == null ? price : discountPrice;
      if (skuParts != null) {
         if (skuParts != null) {
            for (Sku skuPart:skuParts) {
               itemPrice = skuPart.priceToUse.add(itemPrice);
            }
         }
      }
      return itemPrice;
   }

   static Sku findSkuForOptions(List<Sku> skuOptions, List<OptionValue> opts) {
      if (skuOptions != null) {
         for (Sku sku:skuOptions) {
            if (sku.options.equals(opts))
               return sku;
         }
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
         // TODO: need to validate the number of digits match the currency for the store's locale
         removePropError("price");
         invalidPriceStr = null;
      }
      catch (NumberFormatException exc) {
         invalidPriceStr = priceStr;
         addPropError("price", "Invalid price");
      }
      Bind.sendChangedEvent(this, "priceStr");
   }

   @Bindable(manual=true)
   String getPriceStr() {
      if (invalidPriceStr != null)
         return invalidPriceStr;
      return price == null ? "" : price.toString();
   }

   @Bindable(manual=true)
   String getDiscountPriceStr() {
      if (invalidDiscountPriceStr != null)
         return invalidDiscountPriceStr;
      return discountPrice == null ? "" : discountPrice.toString();
   }

   private String invalidDiscountPriceStr = null, invalidPriceStr = null;

   void updateDiscount(String priceStr) {
      try {
         discountPrice = new BigDecimal(priceStr);
         removePropError("discountPrice");
         invalidDiscountPriceStr = null;
      }
      catch (NumberFormatException exc) {
         addPropError("discountPrice", "Invalid discountPrice");
         invalidDiscountPriceStr = priceStr;
         discountPrice = null;
      }
      Bind.sendChangedEvent(this, "discountPriceStr");
   }

   static String validateSkuCode(String sc) {
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

   Sku getSkuForOptionsWith(List<OptionValue> optValues, int overrideIx, OptionValue overrideVal) {
      if (skuOptions == null) {
         return null;
      }
      int numOptions = optValues.size();
      List<Sku> productSkus = skuOptions;
      for (int sx = 0; sx < productSkus.size(); sx++) {
         Sku sku = productSkus.get(sx);

         boolean matched = true;
         List<OptionValue> skuVals = sku.options;
         for (int vx = 0; vx < skuVals.size(); vx++) {
            OptionValue skuVal = skuVals.get(vx);
            OptionValue findVal = vx == overrideIx ? overrideVal : optValues.get(vx);
            if (!findVal.optionValue.equals(skuVal.optionValue)) {
               matched = false;
               break;
            }
         }
         if (matched)
            return sku;
      }
      return null;
   }

   ProductInventory getInventory() {
      return null;
   }
}
