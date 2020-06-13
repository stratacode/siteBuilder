/*
   From: www.geonames.org

   The data format is tab-delimited text in utf8 encoding, with the following fields :

   country code      : iso country code, 2 characters
   postal code       : varchar(20)
   place name        : varchar(180)
   admin name1       : 1. order subdivision (state) varchar(100)
   admin code1       : 1. order subdivision (state) varchar(20)
   admin name2       : 2. order subdivision (county/province) varchar(100)
   admin code2       : 2. order subdivision (county/province) varchar(20)
   admin name3       : 3. order subdivision (community) varchar(100)
   admin code3       : 3. order subdivision (community) varchar(20)
   latitude          : estimated latitude (wgs84)
   longitude         : estimated longitude (wgs84)
   accuracy          : 
*/

import java.util.Arrays;

@DBTypeSettings
class PostalCodeInfo {
   String country;

   // Not making this a primary key so that we can add new rows on the fly - will sort by last modified
   @DBPropertySettings(indexed=true, required=true)
   @FindBy(orderBy="-lastModified")
   String postalCode;

   String city;

   String state;
   String stateCode;

   String county;
   String countyCode;

   String community;
   String communityCode;
   double latitude;
   double longitude;
   int accuracyCode; // accuracy of lat/lng from 1=estimated, 4=geonameid, 6=centroid of addresses or shape

   Date lastModified;

   static List<String> csvProperties = Arrays.asList("country", "postalCode", "city", "state", "stateCode", "county", "countyCode", "community", "communityCode", "latitude", "longitude", "accuracyCode");
}
