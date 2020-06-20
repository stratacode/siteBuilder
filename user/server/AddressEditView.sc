import sc.util.StringUtil;

AddressEditView {
   void updatePostalCode(String postalCode) {
      address.postalCode = postalCode;

      if (!StringUtil.isEmpty(postalCode)) {
         String country = StringUtil.isEmpty(address.country) ? defaultCountry : address.country;
         CountryInfo countryInfo = CountryInfo.findLatestByCountryName(country);
         int pclen = countryInfo.postalCodeLen;
         int inlen = postalCode.length();
         // For zip+4 and others where our DB only has a postal code prefix, trim it to the right length
         if (pclen != 0 && inlen >= pclen) {
            List<PostalCodeInfo> infoList = PostalCodeInfo.findByPostalCode(postalCode.substring(0,pclen));
            if (infoList != null) {
               for (PostalCodeInfo info:infoList) {
                  if (StringUtil.equalStrings(info.country, countryInfo.countryCode)) {
                     address.city = info.city;
                     address.state = info.state;
                     break;
                  }
               }
            }
         }
         address.validateAddress();
      }

   }
}
