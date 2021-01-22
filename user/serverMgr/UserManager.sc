import java.util.Arrays;
import sc.db.Query;

UserManager {
   @Sync(resetState=true)
   void setSearchActive(boolean na) {
      searchActive = na;
      // Normally we don't run the query until the user presses a button, but if the session is being reset this can be true
      // at this time and we need to refresh the results
      if (na && searchResultCount == 0) {
         DynUtil.invokeLater(new Runnable() {
            void run() {
               if (searchResultCount == 0 && searchActive)
                  runSearchQuery();
            }
         }, 0);

      }
   }

   void doSearch(boolean recentOnly) {
      currentPage = 0;
      this.searchRecent = recentOnly;
      runSearchQuery();
      this.searchActive = true;
   }

   void runSearchQuery() {
      boolean isAdmin = siteMgr.userView.user.superAdmin;

      List<UserProfile> newUsers;
      if (searchAcrossSites && isAdmin) {
         List<String> propNames = null;
         List<Object> propValues = null;
         if (!showGuests) {
            propNames.add("registered");
            propValues.add(Boolean.TRUE);
         }
         // TODO: need to support recent here - switch to using query to add the extra clause
         newUsers = (List<UserProfile>) UserProfile.getDBTypeDescriptor().searchQuery(searchText, propNames, propValues, null, Arrays.asList("-userProfileStats.lastActivity"),
                     currentPage*numUsersPerPage, numUsersPerPage);

         if (currentPage == 0) {
            if (newUsers.size() == numUsersPerPage)
               searchResultCount = UserProfile.getDBTypeDescriptor().searchCountQuery(searchText, propNames, propValues);
            else
               searchResultCount = newUsers.size();
         }
      }
      else {
         Date toDate = new Date();
         Date fromDate = searchRecent ? new Date(System.currentTimeMillis() - 7*TextUtil.dayMillis) : new Date(1000);

         // Here we need to find all users that have had sessions on this site, ordered by the most recent session on the site
         /*
         Query findUsersQuery = Query.and(
                Query.includesItem("userSessions", Query.and(Query.eq("site", site), Query.lt("lastModified", toDate), Query.gt("lastModified", fromDate))),
                Query.or(Query.match("emailAddress", searchText), Query.match("firstName", searchText)));

         newUsers = (List<UserProfile>)
            UserProfile.getDBTypeDescriptor().query(findUsersQuery, null, Arrays.asList("userSessions.lastModified"),
                                                    currentPage*numUsersPerPage, numUsersPerPage);
         */
         if (showGuests)
            newUsers = (List<UserProfile>) UserProfile.findUsersForSite(site.id, fromDate, toDate, currentPage*numUsersPerPage, numUsersPerPage);
         else
            newUsers = (List<UserProfile>) UserProfile.findRegUsersForSite(site.id, fromDate, toDate, currentPage*numUsersPerPage, numUsersPerPage);

         if (currentPage == 0) {
            if (newUsers.size() == numUsersPerPage)
               searchResultCount = (int) UserProfile.countUsersForSite(site.id, fromDate, toDate);
            else
               searchResultCount = newUsers.size();
         }
      }

      currentUsers = newUsers;
      if (currentUsers.size() == 0) {
         int numUsers = UserSession.getDBTypeDescriptor().searchCountQuery("", searchSite, searchSiteValues);
         if (numUsers == 0)
            searchStatusMessage = "No users have have visited this site";
         else
            searchStatusMessage = "No sessions found out of: " + numUsers + " sessions for this site";

         currentPage = 0;
         numPages = 0;
      }
      else {
         if (currentPage * numUsersPerPage >= searchResultCount)
            currentPage = 0;
         numPages = (searchResultCount + numUsersPerPage - 1) / numUsersPerPage;
      }

   }

   List<UserSession> getUserSessions(UserProfile user, int curPage) {
      List<UserSession> res = (List<UserSession>) UserSession.findByUser(user, curPage*numSessionsPerPage, numSessionsPerPage);
      return res;
   }

   void gotoPrevPage() {
      if (currentPage == 0) {
         return;
      }
      currentPage = currentPage - 1;
      runSearchQuery();
   }

   void gotoNextPage() {
      if (currentPage >= numPages - 1) {
         return;
      }
      currentPage = currentPage + 1;
      runSearchQuery();
   }

   void updateShowGuests(boolean newVal) {
      if (newVal != showGuests) {
         showGuests = newVal;
         currentPage = 0;
         runSearchQuery();
      }
   }

   void updateLocked(UserProfile profile, boolean newValue) {
      boolean isAdmin = siteMgr.userView.user.superAdmin;
      if (isAdmin) {
         profile.locked = newValue;
      }
   }

   void updateEmailAddress(UserProfile user, String emailAddress) {
      String emailError = user.validateEmailAddress(emailAddress);
      if (emailError != null) {
         emailEditError = emailError;
      }
      else {
         if (user.userbase.useEmailForUserName) {
            user.userName = user.emailAddress;
            UserProfile existing = UserProfile.findByUserName(user.userbase, emailAddress);
            if (existing != null && existing != user) {
               emailEditError = "Account already exists with userName: " + emailAddress;
               return;
            }
            emailEditError = null;
         }
         user.emailAddress = emailAddress;
      }
   }
}
