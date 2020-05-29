import java.util.HashMap;

class Currency {
   String currencyName;
   String symbol;

   Currency(String name, String sym) {
      currencyName = name;
      symbol = sym;
   }

   static HashMap<String,Currency> currencyForLanguageTag = new HashMap<String,Currency>();
   static HashMap<String,Currency> currencyForName = new HashMap<String,Currency>();

   static void addCurrency(Currency c, String languageTag) {
      currencyForLanguageTag.put(languageTag, c);
      currencyForName.put(c.currencyName, c);
   }

   static Currency usDollars = new Currency("USD", "$");
   static Currency caDollars = new Currency("CA", "$");
   static Currency euro = new Currency("EU", "€");
   static Currency englishPound = new Currency("GDP", "£");
   static Currency australianDollar = new Currency("AUD", "$");
   static Currency newZealandDollar = new Currency("NZD", "$");

   static {
      addCurrency(usDollars, "en-US");
      addCurrency(caDollars, "en-CA");
      addCurrency(englishPound, "en-GB");
      addCurrency(australianDollar, "en-AU");
      addCurrency(newZealandDollar, "en-NZ");
      addCurrency(caDollars, "fr-CA");
      addCurrency(euro, "fr-FR");
      addCurrency(euro, "nl-NL");
   }

   static Currency getForName(String name) {
      Currency res = currencyForName.get(name);
      if (res == null)
         res = usDollars;
      return res;
   }
}
