import java.util.Arrays;

SiteManager {
   static private List<String> availableCountries;
   static List<String> getAvailableCountries() {
      if (availableCountries == null) {
         List<CountryInfo> infos = (List<CountryInfo>)CountryInfo.getDBTypeDescriptor().findAll(Arrays.asList("countryName"), -1, -1);
         if (infos == null || infos.size() == 0) {
            System.err.println("*** No countries in country info database");
            return Arrays.asList("United States", "Tanzania", "Netherlands", "Mexico");
         }
         availableCountries = new ArrayList<String>();
         for (CountryInfo countryInfo:infos)
            availableCountries.add(countryInfo.countryName);
      }
      return availableCountries;
   }

   void changeDefaultCurrency(String currencyName) {
      Currency newCurrency = Currency.currencyForName.get(currencyName);
      if (newCurrency != null)
         store.defaultCurrencyName = newCurrency.currencyName;
   }

   void addSupportedCurrency(String newCurrencyName) {
      Currency newCurrency = Currency.currencyForName.get(newCurrencyName);
      if (newCurrency != null && !store.supportedCurrencies.contains(newCurrencyName))
         store.supportedCurrencies.add(newCurrency.currencyName);
   }

   void changeDefaultCountry(String country) {
      store.defaultCountry = country;
   }

   void addCountry(String country) {
      if (!store.countryNames.contains(country))
         store.countryNames.add(country);
   }

   void refreshSiteForUser() {
      super.refreshSiteForUser();
      if (site instanceof Storefront) {
         store = (Storefront) site;
         validateStore();
      }
      else
         validStore = false;
   }

   void changeSite(SiteContext newSite) {
      super.changeSite(newSite);
      if (newSite instanceof Storefront) {
         store = (Storefront) newSite;
         validateStore();
      }
      else {
         store = null;
         validStore = false;
      }
   }

   void validateStore() {
      validStore = true;
      if (store.supportedCurrencies == null) {
         store.supportedCurrencies = new ArrayList<String>();
         store.supportedCurrencies.add("USD");
         if (store.defaultCurrency == null)
            store.defaultCurrencyName = Currency.usDollars.currencyName;
      }
      if (store.countryNames == null) {
         store.countryNames = new ArrayList<String>();
         store.countryNames.add("United States");
      }
   }

   void startAddSite() {
      super.startAddSite();
      validStore = false;
   }

   void completeAddSite() {
      super.completeAddSite();
      if (validSite && site instanceof Storefront) {
         store = (Storefront) site;
         validateStore();
      }
      else {
         validStore = false;
         store = null;
      }
   }

   SiteContext createSite(String siteTypeName) {
      if (siteTypeName.equals(storeSiteName))
         return (Storefront) Storefront.getDBTypeDescriptor().createInstance();
      return super.createSite(siteTypeName);
   }
}
