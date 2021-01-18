GeoIpInfo {
   CityInfo cityInfo;

   transient boolean cityInfoChecked = false;

   @DBPropertySettings(persist=false)
   CityInfo getCityInfo() {
      if (cityInfo == null && !cityInfoChecked) {
         cityInfo = (CityInfo) CityInfo.getDBTypeDescriptor().findById(geoNameId);
         cityInfoChecked = true;
      }
      return cityInfo;
   }

   /*
   PostalCodeInfo postalCodeInfo;

   @DBPropertySettings(persist=false)
   PostalCodeInfo getPostalCodeInfo() {
      if (postalCodeInfo == null && postalCode != null) {
         List<PostalCodeInfo> infos = (List<PostalCodeInfo>) PostalCodeInfo.findByPostalCode(postalCode);
         if (infos != null && infos.size() > 0) {
            for (PostalCodeInfo pci:infos) {
            }
         }
      }
      return postalCodeInfo;
   }
   */

   static GeoIpInfo lookup(String remoteIp) {
      List<? extends GeoIpInfo> res;
      if (remoteIp.contains(":"))
        res = (List<GeoIpInfoV6>) GeoIpInfoV6.queryForIpv6(remoteIp);
      else
        res = (List<GeoIpInfoV4>) GeoIpInfoV4.queryForIpv4(remoteIp);
      if (res == null || res.size() == 0)
         return null;
      if (res.size() > 1) {
         System.out.println("*** More than geoIp match for remoteIp: " + remoteIp);
      }
      return res.get(0);

   }
}
