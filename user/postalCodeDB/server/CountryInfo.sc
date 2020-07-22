CountryInfo {
   static CountryInfo findLatestByCountryName(String countryName) {
      List<CountryInfo> res = findByCountryName(countryName);
      if (res == null || res.size() == 0)
         return null;
      return res.get(0);
   }

}
