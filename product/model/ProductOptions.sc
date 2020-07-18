/** Stores a set of options for a given product, possibly shared by more than one product  */
@DBTypeSettings
@Sync(onDemand=true)
// TODO: rename to OptionScheme
class ProductOptions {
   /* Name of this group of product options */
   @FindBy @DBPropertySettings(indexed=true)
   String optionSchemeName; // TODO: rename schemeName?

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
}
