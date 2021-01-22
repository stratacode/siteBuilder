@Sync
@Component
class BaseManager {
   void resetForm() { }

   void clearFormErrors() {}

   SiteContext site;

   SiteContext oldSite = null;

   SiteManager siteMgr;

   site =: siteChanged();

   void siteChanged() {
      if (oldSite != null && oldSite != site)
         clearSearch();
      oldSite = site;
   }

   void clearSearch() {
      resetForm();
   }

}
