/** 
 * Include this layer to load the address database for various address related
 * features like mapping postal code to city, state, country and the geo ip 
 * database for mapping remoteIp to a city.
 * 
 * TODO: automate this as using the repository as an http package
 * Depends on the allCountries.txt and cities500.txt file being installed from:
 * http://download.geonames.org/export/zip/allCountries.zip
 *  - extract that and rename to data/allCountries.csv, and data/cities500.csv 
 *
 * TODO: put the install steps into a command line wizard to help users set this up
 * For the GeoIp database, this product includes GeoLite2 data created by MaxMind, available from www.maxmind.com
 *
 * Sign up for an account there and download either the free GeoLite2-City-Blocks-IPv4 and v6 files and put them into the
 * data directory, or (I believe) the paid versions use the same format and so should also work.
 *
 * The countryInfo.csv file also comes from the geoNames site but is small enough 
 * it's just added to the source control.
 */
public user.addressDB.loader extends user.addressDB.server, jdbc.pgsql, jdbc.schemaManager,
                                 sys.basicMain, hikari.dataSource {
/*
 * It's a big file so don't want to copy it to the build dir for each build.
 * Instead, using @BuildInit to 
   object csvFileProcessor extends LayerFileProcessor {
      prependLayerPackage = false;
      useSrcDir = false;
      useClassesDir = true;
      extensions = {"csv"};
   }
*/

   String postalCodePath = getRelativeFile("data/allCountries.csv");
   String countryDBPath = getRelativeFile("data/countryInfo.csv");
   String cityPath = getRelativeFile("data/cities500.csv");
   String geoIpV4Path = getRelativeFile("data/GeoLite2-City-Blocks-IPv4.csv");
   String geoIpV6Path = getRelativeFile("data/GeoLite2-City-Blocks-IPv6.csv");

   compiledOnly = true;
}
