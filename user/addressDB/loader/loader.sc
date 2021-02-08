/** 
 * Include this layer to load the address database for various address related
 * features like mapping postal code to city, state, country and the geo ip 
 * database for mapping remoteIp to a city.
 * 
 * TODO: automate this as using the repository as an http package
 * Depends on the allCountries.txt and cities500.txt file being installed from:
 * http://download.geonames.org/export/zip/allCountries.zip
 *  - extract that and rename to data/allCountries.csv
 * In the same directory, download cities500.txt or choose a list of countries to get complete coverage and put those
 * files in the directory: data/cityData with .csv suffixes
 *  (e.g. data/cityData/{cities500.csv,US.csv,... other country files for complete coverage}
 * The countryInfo.csv file also comes from the geoNames site but is small enough
 * it's just added to the source control (or grab a recent copy from the site in case it's changed)
 *
 * TODO: put the install steps into a command line wizard to help users set this up. It would be nice to have a system
 * wide update option for scc that will prompt the user to update the geo database. Currently we try to load it when
 * there are zero rows for a given data type.
 *
 * For the GeoIp database, this product includes GeoLite2 data created by MaxMind, available from www.maxmind.com
 *
 * Sign up for an account there and download either the free GeoLite2-City-Blocks-IPv4 and v6 files and put them into the
 * data directory, or (I believe) the paid versions use the same format and so should also work.
 *
 */
public user.addressDB.loader extends user.addressDB.server, jdbc.pgsql, jdbc.schemaManager,
                                 sys.basicMain, hikari.dataSource {
/*
 * It's a big file so don't want to copy it to the build dir for each build.
 * Instead, using @BuildInit to put the csv filename from the source directory into the runtime.

   object csvFileProcessor extends LayerFileProcessor {
      prependLayerPackage = false;
      useSrcDir = false;
      useClassesDir = true;
      extensions = {"csv"};
   }
*/

   String postalCodePath = getRelativeFile("data/allCountries.csv");
   String countryDBPath = getRelativeFile("data/countryInfo.csv");
   String cityDirPath = getRelativeFile("data/cityData");
   String geoIpV4Path = getRelativeFile("data/GeoLite2-City-Blocks-IPv4.csv");
   String geoIpV6Path = getRelativeFile("data/GeoLite2-City-Blocks-IPv6.csv");

   compiledOnly = true;
}
