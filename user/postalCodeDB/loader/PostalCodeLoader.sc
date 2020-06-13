import java.io.File;

@Component
@CompilerSettings(createOnStartup=true)
object PostalCodeLoader {
   @sc.obj.BuildInit("postalCodePath")
   String csvFileName;

   void init() {
      if (!PostalCodeInfo.getDBTypeDescriptor().getSchemaReady()) {
         System.out.println("PostalCodeLoader - schema not ready");
         return;
      }
      System.out.println("*** Importing csv file: " + csvFileName);
      if (!new File(csvFileName).canRead())
         System.err.println("*** Unable to find postalCodeDB csv file: " + csvFileName);
      else {
         try {
            int res = DBUtil.importCSVFile(csvFileName, PostalCodeInfo.class, "\t", PostalCodeInfo.csvProperties);
            System.out.println("*** Imported: " + res + " postalCode entries");
         }
         catch (IllegalArgumentException exc) {
            System.err.println("*** Error importing CSV file: " + exc);
         }
      }
   }
}
