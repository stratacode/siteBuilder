import java.io.File;

@Component
@CompilerSettings(createOnStartup=true)
object GeoIpLoader {
   @sc.obj.BuildInit("geoIpV4Path")
   String geoIpV4FileName;

   @sc.obj.BuildInit("geoIpV6Path")
   String geoIpV6FileName;;

   void init() {
      DBTypeDescriptor geoIpV4Type = GeoIpInfoV4.getDBTypeDescriptor();
      if (!geoIpV4Type.getSchemaReady()) {
         System.out.println("GeoIpInfo - schema not ready - waiting to load data");
         geoIpV4Type.getDataSource().runWhenReady(new Runnable() {
            void run() {
               System.out.println("GeoIpInfo - schema is now ready - loading data");
               loadData();
            }
          });
         return;
      }
      loadData();
   }

   void loadData() {
      DBTypeDescriptor geoIpV4Type = GeoIpInfoV4.getDBTypeDescriptor();

      int numEntries = geoIpV4Type.countAll();
      long startTime = System.currentTimeMillis();
      if (numEntries == 0) {
         System.out.println("*** Found no geoIp v4 records - importing geoIp DB file: " + geoIpV4FileName);
         if (!new File(geoIpV4FileName).canRead())
            System.err.println("*** Unable to find geoIp csv file: " + geoIpV4FileName);
         else {
            try {
               int res = DBUtil.importCSVFile(geoIpV4FileName, GeoIpInfoV4.class, ",", true, null, GeoIpInfo.csvProperties, null, false);
               System.out.println("*** Imported: " + res + " geoIp entries - v4 in: " + sc.util.PerfMon.getTimeDelta(startTime));
            }
            catch (IllegalArgumentException exc) {
               System.err.println("*** Error importing geoIp v4 csv file: " + exc);
            }
         }
      }
      else
         System.out.println("Found: " + numEntries + " geoIPv4 entries - skipping load");

      DBTypeDescriptor geoIpV6Type = GeoIpInfoV6.getDBTypeDescriptor();
      if (!geoIpV6Type.getSchemaReady()) {
         System.out.println("GeoIpLoader - schema not available. Use the schema commandline to update the schema and rerun the loader ");
         return;
      }

      numEntries = geoIpV6Type.countAll();
      startTime = System.currentTimeMillis();
      if (numEntries == 0) {
         System.out.println("*** Found no geoIp v6 records - importing geoIp DB file: " + geoIpV6FileName);
         if (!new File(geoIpV6FileName).canRead())
            System.err.println("*** Unable to find geoIp csv file: " + geoIpV6FileName);
         else {
            try {
               int res = DBUtil.importCSVFile(geoIpV6FileName, GeoIpInfoV6.class, ",", true, null, GeoIpInfo.csvProperties);
               System.out.println("*** Imported: " + res + " geoIp entries - v6 in: " + sc.util.PerfMon.getTimeDelta(startTime));
            }
            catch (IllegalArgumentException exc) {
               System.err.println("*** Error importing geoIp v6 csv file: " + exc);
            }
         }
      }
      else
         System.out.println("Found: " + numEntries + " geoIp v6 entries - skipping load");
   }
}
