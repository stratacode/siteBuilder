CountryInfo {
   static CountryInfo findLatestByCountryName(String countryName) {
      List<CountryInfo> res = findByCountryName(countryName);
      if (res == null)
         return null;
      return res.get(0);
   }

}
