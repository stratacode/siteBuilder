import java.io.File;

@Component
@CompilerSettings(createOnStartup=true)
object PostalCodeLoader {
   @sc.obj.BuildInit("postalCodePath")
   String postalCodeFileName;

   @sc.obj.BuildInit("countryDBPath")
   String countryDBFileName;

   void init() {
      DBTypeDescriptor postalCodeType = PostalCodeInfo.getDBTypeDescriptor();
      if (!postalCodeType.getSchemaReady()) {
         System.out.println("PostalCodeInfo - schema not ready - waiting to load data");
         postalCodeType.getDataSource().runWhenReady(new Runnable() {
            void run() {
               System.out.println("PostalCodeInfo - schema is now ready - loading data");
               loadData();
            }
          });
         return;
      }
      loadData();
   }

   void loadData() {
      DBTypeDescriptor postalCodeType = PostalCodeInfo.getDBTypeDescriptor();
      int numCodes = postalCodeType.countAll();
      if (numCodes == 0) {
         System.out.println("*** Found no postalCode records - importing postalCode DB file: " + postalCodeFileName);
         if (!new File(postalCodeFileName).canRead())
            System.err.println("*** Unable to find addressDB csv file: " + postalCodeFileName);
         else {
            try {
               int res = DBUtil.importCSVFile(postalCodeFileName, PostalCodeInfo.class, "\t", false, PostalCodeInfo.csvProperties);
               System.out.println("*** Imported: " + res + " postalCode entries");
            }
            catch (IllegalArgumentException exc) {
               System.err.println("*** Error importing postalCode csv file: " + exc);
            }
         }
      }
      else
         System.out.println("PostalCode DB contains: " + numCodes);

      int numCountries = CountryInfo.getDBTypeDescriptor().countAll();
      if (numCountries == 0) {
         System.out.println("*** Importing country DB file: " + countryDBFileName);
         if (!new File(countryDBFileName).canRead())
            System.err.println("*** Unable to find countryDB csv file: " + countryDBFileName);
         else {
            try {
               int res = DBUtil.importCSVFile(countryDBFileName, CountryInfo.class, "\t", false, CountryInfo.csvProperties);
               System.out.println("*** Imported: " + res + " country entries");
            }
            catch (IllegalArgumentException exc) {
               System.err.println("*** Error importing country csv file: " + exc);
            }
         }
      }
      else
         System.out.println("Country DB contains: " + numCountries);
   }

}
