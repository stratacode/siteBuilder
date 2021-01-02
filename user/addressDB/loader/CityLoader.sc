import java.io.File;

@Component
@CompilerSettings(createOnStartup=true)
object CityLoader {
   @sc.obj.BuildInit("cityPath")
   String cityFileName;

   void init() {
      DBTypeDescriptor cityInfoType = CityInfo.getDBTypeDescriptor();
      if (!cityInfoType.getSchemaReady()) {
         System.out.println("CityInfo - schema not available. Use the schema commandline to update the schema and rerun the loader ");
         return;
      }

      int numCities = cityInfoType.countAll();
      if (numCities == 0) {
         System.out.println("*** Found no city records - importing city DB file: " + cityFileName);
         if (!new File(cityFileName).canRead())
            System.err.println("*** Unable to find city csv file: " + cityFileName);
         else {
            try {
               int res = DBUtil.importCSVFile(cityFileName, CityInfo.class, "\t", false, CityInfo.csvProperties);
               System.out.println("*** Imported: " + res + " city entries");
            }
            catch (IllegalArgumentException exc) {
               System.err.println("*** Error importing city csv file: " + exc);
            }
         }
      }
      else
         System.out.println("City DB contains: " + numCities);
   }
}
