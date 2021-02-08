import java.io.File;

@Component
@CompilerSettings(createOnStartup=true)
object CityLoader {
   @sc.obj.BuildInit("cityDirPath")
   String cityDirPath;

   boolean doUpdate = true;

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
      if (numCities == 0 || doUpdate) {
         boolean hasAny = numCities > 0;
         File cityDir = new File(cityDirPath);
         if (!cityDir.isDirectory())
            System.err.println("*** Missing directory of city files at path: " + cityDirPath);
         else {
            File[] cityFiles = cityDir.listFiles();
            if (cityFiles.length == 0)
               System.out.println("*** Found no city csv files in: " + cityDirPath);
            for (File f:cityFiles) {
               String path = f.getName();
               String ext = sc.util.FileUtil.getExtension(path);
               if (ext.equalsIgnoreCase("csv")) {
                  if (!f.canRead())
                     System.err.println("*** Unable to read city csv file: " + path);
                  try {
                     int res = DBUtil.importCSVFile(f.getAbsolutePath(), CityInfo.class, "\t", false, null, CityInfo.csvProperties, CityInfo.idProperty, hasAny);
                     System.out.println("*** Imported: " + res + " city entries in " + sc.util.PerfMon.getTimeDelta(startTime));
                     if (res > 0)
                        hasAny = true;
                  }
                  catch (IllegalArgumentException exc) {
                     System.err.println("*** Error importing city csv file: " + exc);
                  }
               }
               else
                  System.out.println("*** skipping city file without csv suffix: " + path);
            }
         }
      }
      else
         System.out.println("Found: " + numCities + " records - skipping load");
   }
}
