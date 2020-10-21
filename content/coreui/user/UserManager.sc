import java.util.HashMap;

@sc.js.JSSettings(dependentTypes="sc.content.ManagedImage,sc.user.PageEvent")
@sc.obj.SyncTypeFilter(typeNames={"sc.content.ManagedImage", "sc.user.PageEvent"})
class UserManager extends BaseManager {
   SiteContext site;

   site =: siteChanged();

   String searchText = "";
   @Sync(initDefault=true)
   List<UserProfile> currentUsers;
   UserProfile currentUser;

   boolean searchAcrossSites = false;
   boolean showGuests = true;
   boolean showSessions = true;

   int numUsersPerPage = 20;
   int startIndex = 0;

   @Sync(initDefault=true)
   HashMap<String, List<UserSession>> sessionsByUserId;

   void siteChanged() {
      clearSearch();
   }

   void clearSearch() {
      searchText = "";
      currentUsers = null;
      currentUser = null;
   }

   void updateCurrentUser(UserProfile newUser) {
      if (newUser == currentUser)
         currentUser = null;
      else
         currentUser = newUser;
   }
}
