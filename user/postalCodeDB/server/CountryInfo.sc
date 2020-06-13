CountryInfo {
   static String convertNameToCode(String name) {
      List<CountryInfo> res = findByCountryName(name);
      if (res == null)
         return null;
      return res.get(0).countryCode;
   }
}
