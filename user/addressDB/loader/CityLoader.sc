import java.io.File;

@Component
@CompilerSettings(createOnStartup=true)
object CityLoader {
   @sc.obj.BuildInit("cityPath")
   String cityFileName;

   void init() {
      DBTypeDescriptor cityInfoType = CityInfo.getDBTypeDescriptor();
      if (!cityInfoType.getSchemaReady()) {
         System.out.println("CityInfo - schema not ready - waiting to load data");
         cityInfoType.getDataSource().runWhenReady(new Runnable() {
            void run() {
               System.out.println("CityInfo - schema is now ready - loading data");
               loadData();
            }
          });
         return;
      }
      loadData();
   }

   void loadData() {
      DBTypeDescriptor cityInfoType = CityInfo.getDBTypeDescriptor();
      int numCities = cityInfoType.countAll();
      long startTime = System.currentTimeMillis();
      if (numCities == 0) {
         System.out.println("*** Found no city records - importing city DB file: " + cityFileName);
         if (!new File(cityFileName).canRead())
            System.err.println("*** Unable to find city csv file: " + cityFileName);
         else {
            try {
               int res = DBUtil.importCSVFile(cityFileName, CityInfo.class, "\t", false, CityInfo.csvProperties);
               System.out.println("*** Imported: " + res + " city entries in " + sc.util.PerfMon.getTimeDelta(startTime));
            }
            catch (IllegalArgumentException exc) {
               System.err.println("*** Error importing city csv file: " + exc);
            }
         }
      }
      else
         System.out.println("Found: " + numCities + " records - skipping load");
   }
}
