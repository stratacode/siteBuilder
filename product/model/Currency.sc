class Currency {
   String currencyName;
   String symbol;

   Currency(String name, String sym) {
      currencyName = name;
      symbol = sym;
   }

   static Currency usDollars = new Currency("USD", "$");
   static Currency euro = new Currency("EU", "€");
}
