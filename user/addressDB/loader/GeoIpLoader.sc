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
         System.out.println("GeoIpLoader - schema not available. Use the schema commandline to update the schema and rerun the loader ");
         return;
      }

      int numEntries = geoIpV4Type.countAll();
      if (numEntries == 0) {
         System.out.println("*** Found no geoIp v4 records - importing geoIp DB file: " + geoIpV4FileName);
         if (!new File(geoIpV4FileName).canRead())
            System.err.println("*** Unable to find geoIp csv file: " + geoIpV4FileName);
         else {
            try {
               int res = DBUtil.importCSVFile(geoIpV4FileName, GeoIpInfoV4.class, ",", true, GeoIpInfo.csvProperties);
               System.out.println("*** Imported: " + res + " geoIp entries - v4");
            }
            catch (IllegalArgumentException exc) {
               System.err.println("*** Error importing geoIp v4 csv file: " + exc);
            }
         }
      }
      else
         System.out.println("GeoIp DB contains: " + numEntries + " v4 entries");

      DBTypeDescriptor geoIpV6Type = GeoIpInfoV6.getDBTypeDescriptor();
      if (!geoIpV6Type.getSchemaReady()) {
         System.out.println("GeoIpLoader - schema not available. Use the schema commandline to update the schema and rerun the loader ");
         return;
      }

      numEntries = geoIpV6Type.countAll();
      if (numEntries == 0) {
         System.out.println("*** Found no geoIp v6 records - importing geoIp DB file: " + geoIpV6FileName);
         if (!new File(geoIpV6FileName).canRead())
            System.err.println("*** Unable to find geoIp csv file: " + geoIpV6FileName);
         else {
            try {
               int res = DBUtil.importCSVFile(geoIpV6FileName, GeoIpInfoV6.class, ",", true, GeoIpInfo.csvProperties);
               System.out.println("*** Imported: " + res + " geoIp entries - v6");
            }
            catch (IllegalArgumentException exc) {
               System.err.println("*** Error importing geoIp v6 csv file: " + exc);
            }
         }
      }
      else
         System.out.println("GeoIp DB contains: " + numEntries + " v6 entries");
   }
}
