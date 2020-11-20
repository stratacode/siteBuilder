@DBTypeSettings(typeId=1)
@Sync(onDemand=true)
class SiteContext implements IPropValidator {
   @Sync(syncMode=SyncMode.ServerToClient,initDefault=true)
   List<UserProfile> siteAdmins;

   @Sync(syncMode=SyncMode.Disabled)
   Userbase userbase;

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

   @Sync(syncMode=SyncMode.ServerToClient,initDefault=true)
   MediaManager mediaManager;

   // Configurable presentation elements - add to the page menu, add new pages and provide the default page
   @Sync(initDefault=true)
   List<BaseMenuItem> menuItems;

   boolean visible = true;

   Date lastModified;

   String toString() {
      return siteName;
   }

   String getSiteTypeName() {
      return "Site";
   }

   String getSitePathTypeName() {
      return "sites";
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

   static String validateIcon(String url) {
      return ManagedResource.validateURL(url, "site icon URL", false);
   }

   String homeUrl := "/" + sitePathTypeName + "/" + sitePathName;

   boolean isSiteAdmin(UserProfile profile) {
      return siteAdmins != null && siteAdmins.contains(profile);
   }
}
