/** 
 * Depends on the allCountries.txt file being installed from:
 * http://download.geonames.org/export/zip/allCountries.zip
 *  - extract that and rename to allCountries.txt  
 * TODO: automate this as a download package
 *
 * To update the postal_code_len column from the postal_code_info table use: 
 *    update country_info set postal_code_len = length(postal_code_info.postal_code) from postal_code_info where postal_code_info.country = country_info.country_code;
 *
 * To turn the countries table back into csv use: copy country_info(country_name,country_code,postal_code_len) to stdout delimiter E'\t';
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
