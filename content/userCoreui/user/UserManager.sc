import java.util.HashMap;

class UserManager extends BaseManager {
   SiteContext site;

   site =: siteChanged();

   String searchText = "";
   List<UserProfile> currentUsers;
   UserProfile currentUser;

   boolean searchAcrossSites = false;
   boolean showGuests = true;
   boolean showSessions = true;

   int numUsersPerPage = 20;
   int startIndex = 0;

   HashMap<Long, List<UserSession>> sessionsByUserId;

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
