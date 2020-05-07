/** Stores a set of options for a given product, possibly shared by more than one product  */
@DBTypeSettings
class ProductOptions {
   /* Name of this group of product options */
   String optionTypeName;
   List<ProductOption> options;
}
