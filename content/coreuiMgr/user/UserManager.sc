import java.util.HashMap;

@sc.js.JSSettings(dependentTypes="sc.content.ManagedImage,sc.user.PageEvent")
@sc.obj.SyncTypeFilter(typeNames={"sc.content.ManagedImage", "sc.user.PageEvent"})
class UserManager extends BaseManager {
   SiteContext site;

   site =: siteChanged();

   @Sync(initDefault=true, resetState=true)
   String searchText = "";
   @Sync(initDefault=true)
   List<UserProfile> currentUsers;
   UserProfile currentUser;

   @Sync(initDefault=true, resetState=true)
   boolean searchAcrossSites = false;
   @Sync(initDefault=true, resetState=true)
   boolean showGuests = true;
   @Sync(initDefault=true, resetState=true)
   boolean showSessions = true;

   @Sync(initDefault=true, resetState=true)
   int numUsersPerPage = 20;
   @Sync(initDefault=true, resetState=true)
   int startIndex = 0;

   @Sync(initDefault=true)
   String searchStatusMessage = null;

   @Sync(initDefault=true)
   HashMap<String, List<UserSession>> sessionsByUserId;

   @Sync(initDefault=true)
   String emailEditError;

   void siteChanged() {
      clearSearch();
   }

   void clearSearch() {
      searchText = "";
      currentUsers = null;
      currentUser = null;
      searchStatusMessage = null;
   }
}
