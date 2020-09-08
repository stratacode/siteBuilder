import java.util.Arrays;
import sc.db.Query;

UserManager {
   void doSearch() {
      boolean isAdmin = siteMgr.userView.user.superAdmin;

      HashMap<Long,List<UserSession>> newSessionsById = new HashMap<Long,List<UserSession>>();

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
               newSessionsById.put(user.id, UserSession.findByUser(user));
            }
         }
      }
      else {
         newUsers = new ArrayList<UserProfile>();
      }
      if (showSessions) {
         //List<UserSession> sessions = (List<UserSession>) UserSession.findBySite(site, startIndex, numUsersPerPage);
         List<UserSession> sessions = (List<UserSession>) UserSession.getDBTypeDescriptor().query(
             Query.and(Query.eq("site", site),
                       Query.or(Query.match("user.emailAddress", searchText),
                                Query.match("user.firstName", searchText))), null, null, startIndex, numUsersPerPage);

         if (sessions != null) {
            for (int i = 0; i < sessions.size(); i++) {
               UserSession session = sessions.get(i);
               UserProfile user = session.user;
               if (!showGuests && !user.registered)
                  continue;
               List<UserSession> userSessions = newSessionsById.get(user.id);
               if (userSessions == null) {
                  userSessions = new ArrayList<UserSession>();
                  newSessionsById.put(user.id, userSessions);
                  newUsers.add(user);
               }
               userSessions.add(session);
            }
         }
         sessionsByUserId = newSessionsById;
      }
      //searchForExtraUsers(newUsers);
      currentUsers = newUsers;
   }

   void updateLocked(UserProfile profile, boolean newValue) {
      boolean isAdmin = siteMgr.userView.user.superAdmin;
      if (isAdmin) {
         profile.locked = newValue;
      }
   }
}
