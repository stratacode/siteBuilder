
/** 
  When you set the @OptionProperty annotation on a property in a Product, an instance of this
  class is generated.  Each product can retrieve it's list of PropertyOption's that are defined on it and it's super
  types, in the order specified by priority. 
  */
class ProductOption {
   String propertyName;
   String skuPattern; 
}


