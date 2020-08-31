/** Stores a set of options for a given product, possibly shared by more than one product  */
@DBTypeSettings
@Sync(onDemand=true)
class OptionScheme implements IPropValidator {
   /* Name of this group of product options */
   @FindBy(with="store")
   @DBPropertySettings(indexed=true,required=true)
   String schemeName;

   @DBPropertySettings(indexed=true)
   Storefront store;

   @DBPropertySettings(columnType="jsonb")
   List<ProductOption> options;

   Date lastModified;

   List<OptionValue> getDefaultOptions() {
      ArrayList<OptionValue> res = new ArrayList<OptionValue>();
      for (ProductOption opt:options) {
         if (opt.defaultValue == null)
            res.add(opt.optionValues.get(0));
         else
            res.add(opt.defaultValue);
      }
      return res;
   }

   List<String> getDefaultOptionFilter() {
      if (options == null)
         return null;
      int num = options.size();
      ArrayList<String> res = new ArrayList<String>(num);
      for (int i = 0; i < num; i++) {
         res.add(options.get(i).anyString);
      }
      return res;
   }

   @Bindable
   @DBPropertySettings(persist=false)
   Map<String,String> propErrors = null;

   String validateSchemeName(String scheme) {
      return IPropValidator.validateRequired("scheme name", scheme);
   }

   void validateOptionScheme() {
      propErrors = DynUtil.validateProperties(this, null);
      if (options == null || options.size() == 0)
         addPropError("options", "Must have at least one option");
      else {
         for (ProductOption opt:options) {
            if (opt.optionValues == null || opt.optionValues.size() == 0)
               addPropError("options", "Option missing values");
            else if (opt.defaultValue == null)
               addPropError("options", "Option missing default value");
            else {
               for (OptionValue optVal:opt.optionValues) {
                  String err = IPropValidator.validateRequired("option value", optVal.optionValue);
                  if (err != null)
                     addPropError("options", err);
                  else {
                     err = IPropValidator.validateRequired("sku symbol", optVal.skuSymbol);
                     if (err != null)
                        addPropError("options", err);
                  }
               }
            }
         }
      }
   }
}
