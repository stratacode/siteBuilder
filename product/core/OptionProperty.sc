@interface OptionProperty {
   int priority; // Affects the order of the option in the product's option list, which defaults to the 
                 // order of the option property in the products source.
   String skuPattern = "-{value}-";
}

