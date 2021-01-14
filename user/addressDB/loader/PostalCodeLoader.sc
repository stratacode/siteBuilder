import java.io.File;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
               int res = DBUtil.importCSVFile(postalCodeFileName, PostalCodeInfo.class, "\t", false, null, PostalCodeInfo.csvProperties);
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
            Connection conn = null;
            PreparedStatement ps = null;
            try {
               int res = DBUtil.importCSVFile(countryDBFileName, CountryInfo.class, "\t", false, "#", CountryInfo.csvProperties);
               System.out.println("*** Imported: " + res + " country entries");
                
               DataSource dataSource = CountryInfo.getDBTypeDescriptor().getDataSource().getDataSource();
               conn = dataSource.getConnection();
               ps = conn.prepareStatement("update country_info set postal_code_len = length(postal_code_info.postal_code) from postal_code_info where postal_code_info.country = country_info.country_code");
               int updCt = ps.executeUpdate();
               System.out.println("*** Updated postalCodeLen for: " + updCt + " records");
            }
            catch (IllegalArgumentException exc) {
               System.err.println("*** Error importing country csv file: " + exc);
            }
            catch (SQLException exc) {
               System.err.println("*** SQL error updating postal code len: " + exc);
            }
            finally {
               DBUtil.close(conn, ps);
            }
         }
      }
      else
         System.out.println("Country DB contains: " + numCountries);
   }

}
