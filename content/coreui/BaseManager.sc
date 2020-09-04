@Sync
@Component
class BaseManager {
   void resetForm() { }

   void clearFormErrors() {}

   SiteContext site;

   SiteManager siteMgr;

   site =: siteChanged();

   void siteChanged() {
      clearSearch();
   }

   void clearSearch() {
      resetForm();
   }

}
