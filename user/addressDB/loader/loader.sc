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
 * For the GeoIp database, this product includes GeoLite2 data created by MaxMind, available from
 * <a href="https://www.maxmind.com">https://www.maxmind.com</a>.
 *
 * Sign up for an account there and download either the free GeoLite2-City-Blocks-IPv4 and v6 files and put them into the
 * data directory, or (I believe) the paid versions use the same format and so should also work.
 *
 * The countries.csv file is in source control. It was generated from a standard countries.csv file with name, abbrev
 * where we added a postal_code_len column computed from the above allCountries.txt file.  Create the schema and import
 * the raw file leaving the column blank. Then run the psql command:
 *    update country_info set postal_code_len = length(postal_code_info.postal_code) from postal_code_info where postal_code_info.country = country_info.country_code;
 * Then turn the countries table back into csv use the psql command: copy country_info(country_name,country_code,postal_code_len) to stdout delimiter E'\t';
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
   String countryDBPath = getRelativeFile("data/countries.csv");
   String cityPath = getRelativeFile("data/cities500.csv");
   String geoIpV4Path = getRelativeFile("data/GeoLite2-City-Blocks-IPv4.csv");
   String geoIpV6Path = getRelativeFile("data/GeoLite2-City-Blocks-IPv6.csv");

   compiledOnly = true;
}
