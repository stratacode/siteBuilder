// Depends on the allCountries.txt file being installed from:
// http://download.geonames.org/export/zip/allCountries.zip
//  - extract that and rename to allCountries.txt  
// TODO: automate this as a download package
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
