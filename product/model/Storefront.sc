/** The top-level model object for an online store */
@DBTypeSettings
class Storefront {
   String storeName;
   @FindBy
   String storePathName;
   Currency defaultCurrency;
   Category root;
}