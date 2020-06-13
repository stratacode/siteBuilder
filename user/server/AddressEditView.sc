import sc.util.StringUtil;

AddressEditView {
   void updatePostalCode(String postalCode) {
      address.postalCode = postalCode;

      if (!StringUtil.isEmpty(postalCode)) {
         String country = StringUtil.isEmpty(address.country) ? defaultCountry : address.country;
         List<PostalCodeInfo> infoList = PostalCodeInfo.findByPostalCode(postalCode);
         String countryCode = CountryInfo.convertNameToCode(country);
         if (infoList != null) {
            for (PostalCodeInfo info:infoList) {
               if (StringUtil.equalStrings(info.country, countryCode)) {
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
