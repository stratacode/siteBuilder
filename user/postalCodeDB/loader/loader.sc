/** 
 * Depends on the allCountries.txt file being installed from:
 * http://download.geonames.org/export/zip/allCountries.zip
 *  - extract that and rename to allCountries.txt in the data directory for the layer
 * TODO: automate this as using the repository as an http package
 *
 * The countries.csv file is in source control. It was generated from a standard countries.csv file with name, abbrev
 * where we added a postal_code_len column computed from the above allCountries.txt file.  Create the schema and import
 * the raw file leaving the column blank. Then run the psql command:
 *    update country_info set postal_code_len = length(postal_code_info.postal_code) from postal_code_info where postal_code_info.country = country_info.country_code;
 * Then turn the countries table back into csv use the psql command: copy country_info(country_name,country_code,postal_code_len) to stdout delimiter E'\t';
 *
 */
public user.postalCodeDB.loader extends user.postalCodeDB.server, jdbc.pgsql, jdbc.schemaManager,
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
}
