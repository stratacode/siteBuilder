/*
   From: www.maxmind.com

   The data format is comma-delimited text in utf8 encoding, with the following fields :

   network,geoname_id,registered_country_geoname_id,represented_country_geoname_id,is_anonymous_proxy,is_satellite_provider,postal_code,latitude,longitude,accuracy_radius

   See this for the postgresql mapping info:
      https://www.compose.com/articles/storing-network-addresses-using-postgresql/
*/

import java.util.Arrays;

abstract class GeoIpInfo {
   @DBPropertySettings(columnType="cidr")
   String network;

   int geoNameId;
   Integer registeredCountryGeonameId;
   Integer representedCountryGeonameId;
   String postalCode;
   double latitude, longitude;
   int accuracyRadius;

   static List<String> csvProperties =
        Arrays.asList("network", "geoNameId", "registeredCountryGeonameId",
                      "representedCountryGeonameId", null, null, "postalCode", "latitude", "longitude", "accuracyRadius");

}
