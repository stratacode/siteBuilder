@DBTypeSettings(typeId=1)
class SiteContext implements IPropValidator {
   List<UserProfile> siteAdmins;

   // Inner class used without DBTypeSettings with FindBy. Generates a findBy method that will create
   // an instance of the inner class (and a prototype of the outer one if necessary), populate properties
   // from the findBy parameters and return the typed result
   //     List<SiteContext> res = proto.siteForAdmin.findBy(currentUser, true);

   // @FindBy
   //class siteForAdmin {
   //   UserProfile currentUser;
   //   boolean userIsAdmin := sitedmins.contains(currentUser);
   //}

   @Sync(resetState=true,initDefault=true)
   String siteName;

   @FindBy(findOne=true)
   @Sync(resetState=true,initDefault=true)
   String sitePathName;

   String icon;

   MediaManager mediaManager;

   // Configurable presentation elements - add to the page menu, add new pages and provide the default page
   List<BaseMenuItem> menuItems;

   boolean visible = true;

   public String toString() {
      return siteName;
   }

   @Bindable
   @DBPropertySettings(persist=false)
   Map<String,String> propErrors = null;

   static String validateSiteName(String siteName) {
      return IPropValidator.validateRequired("site name", siteName);
   }

   static String validateSitePathName(String sitePathName) {
      return ManagedResource.validatePathName("site path name", sitePathName);
   }

}
