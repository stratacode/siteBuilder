import java.util.Arrays;
import sc.db.Query;

UserManager {
   void doSearch() {
      boolean isAdmin = siteMgr.userView.user.superAdmin;

      HashMap<String,List<UserSession>> newSessionsById = new HashMap<String,List<UserSession>>();

      List<UserProfile> newUsers;
      if (searchAcrossSites && isAdmin) {
         List<String> propNames = null;
         List<Object> propValues = null;
         if (!showGuests) {
            propNames.add("registered");
            propValues.add(Boolean.TRUE);
         }
         newUsers = (List<UserProfile>) UserProfile.getDBTypeDescriptor().searchQuery(searchText, propNames, propValues, null, Arrays.asList("-userProfileStats.lastActivity"),
                     startIndex, numUsersPerPage);
         if (showSessions) {
            for (UserProfile user:newUsers) {
               newSessionsById.put(String.valueOf(user.id), UserSession.findByUser(user));
            }
         }
      }
      else {
         newUsers = new ArrayList<UserProfile>();
      }

      //List<UserSession> sessions = (List<UserSession>) UserSession.findBySite(site, startIndex, numUsersPerPage);
      List<UserSession> sessions = (List<UserSession>) UserSession.getDBTypeDescriptor().query(
          Query.and(Query.eq("site", site),
                    Query.or(Query.match("user.emailAddress", searchText),
                             Query.match("user.firstName", searchText))), null, Arrays.asList("-lastModified"), startIndex, numUsersPerPage);

      if (sessions != null) {
         for (int i = 0; i < sessions.size(); i++) {
            UserSession session = sessions.get(i);
            UserProfile user = session.user;
            if (!showGuests && !user.registered)
               continue;
            List<UserSession> userSessions = newSessionsById.get(String.valueOf(user.id));
            if (userSessions == null) {
               userSessions = new ArrayList<UserSession>();
               newSessionsById.put(String.valueOf(user.id), userSessions);
               newUsers.add(user);
            }
            userSessions.add(session);
         }
      }
      sessionsByUserId = newSessionsById;

      currentUsers = newUsers;
      if (currentUsers.size() == 0) {
         int numUsers = UserSession.getDBTypeDescriptor().searchCountQuery("", searchSite, searchSiteValues);
         if (numUsers == 0)
            searchStatusMessage = "No users have have visited this site";
         else
            searchStatusMessage = "No sessions found out of: " + numUsers + " sessions for this site";
      }
   }

   void updateShowGuests(boolean newVal) {
      if (newVal != showGuests) {
         showGuests = newVal;
         doSearch();
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
