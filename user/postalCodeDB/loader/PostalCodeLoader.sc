import java.io.File;

@Component
@CompilerSettings(createOnStartup=true)
object PostalCodeLoader {
   @sc.obj.BuildInit("postalCodePath")
   String postalCodeFileName;

   @sc.obj.BuildInit("countryDBPath")
   String countryDBFileName;

   void init() {
      if (!PostalCodeInfo.getDBTypeDescriptor().getSchemaReady()) {
         System.out.println("PostalCodeLoader - schema not ready");
         return;
      }

      System.out.println("*** Importing postalCode DB file: " + postalCodeFileName);
      if (!new File(postalCodeFileName).canRead())
         System.err.println("*** Unable to find postalCodeDB csv file: " + postalCodeFileName);
      else {
         try {
            int res = DBUtil.importCSVFile(postalCodeFileName, PostalCodeInfo.class, "\t", PostalCodeInfo.csvProperties);
            System.out.println("*** Imported: " + res + " postalCode entries");
         }
         catch (IllegalArgumentException exc) {
            System.err.println("*** Error importing postalCode csv file: " + exc);
         }
      }

      System.out.println("*** Importing country DB file: " + countryDBFileName);
      if (!new File(countryDBFileName).canRead())
         System.err.println("*** Unable to find countryDB csv file: " + countryDBFileName);
      else {
         try {
            int res = DBUtil.importCSVFile(countryDBFileName, CountryInfo.class, "\t", CountryInfo.csvProperties);
            System.out.println("*** Imported: " + res + " country entries");
         }
         catch (IllegalArgumentException exc) {
            System.err.println("*** Error importing country csv file: " + exc);
         }
      }
   }
}
