import java.util.HashMap;

@Sync(constant=true)
class Currency implements sc.obj.IObjectId {
   String currencyName;
   String symbol;

   Currency(String name, String sym) {
      currencyName = name;
      symbol = sym;
   }

   final static HashMap<String,Currency> currencyForLanguageTag = new HashMap<String,Currency>();
   final static HashMap<String,Currency> currencyForName = new HashMap<String,Currency>();

   static Currency getCurrencyForName(String name) {
      initCurrencies();
      return currencyForName.get(name);
   }

   static void addCurrency(Currency c, String languageTag) {
      currencyForLanguageTag.put(languageTag, c);
      currencyForName.put(c.currencyName, c);
   }

   final static Currency usDollars = new Currency("USD", "$");
   final static Currency caDollars = new Currency("CA", "$");
   final static Currency euro = new Currency("EU", "€");
   final static Currency englishPound = new Currency("GDP", "£");
   final static Currency australianDollar = new Currency("AUD", "$");
   final static Currency newZealandDollar = new Currency("NZD", "$");

   private static boolean inited = false;

   static void initCurrencies() {
      if (inited)
         return;
      inited = true;
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
      initCurrencies();
      Currency res = currencyForName.get(name);
      if (res == null)
         res = usDollars;
      return res;
   }

   public String getObjectId() {
      return "Current_" + currencyName;
   }

   private static List<String> currencyNames = null;
   @Bindable(manual=true) @Constant
   public static List<String> getCurrencyNames() {
      if (currencyNames == null) {
         initCurrencies();
         currencyNames = new ArrayList<String>(currencyForName.keySet());
      }
      return currencyNames;
   }
}
