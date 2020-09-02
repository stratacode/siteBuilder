import java.util.HashMap;

UserView {
   Map<Long,UserSession> userSessions;

   void init() {
      if (userAuthToken != null) {
         user = UserProfile.findByAuthToken(userAuthToken);
         if (user == null || user.userbase != userbase)
            userAuthToken = null; // Invalid token
      }
      if (user == null && userName.length() > 0 && password.length() > 0) {
         login();
      }

      if (user == null && userbase.createAnonymousUser) {
         resetUser();
         userAuthToken = user.createAuthToken();
         persistAuthToken(userAuthToken);
      }

      if (user != null && user.registered) {
         loginStatus = LoginStatus.LoggedIn;
      }
   }

   void resetUser() {
      user = new UserProfile();
      user.userbase = userbase;
      user.initDefaultFields();

      DBUtil.addTestIdInstance(user, "new-anon-profile");
   }

   boolean login() {
      loginStatus = LoginStatus.NotLoggedIn;

      clearErrors();
      String userNameError = user.validateUserName(userName);
      if (userNameError != null) {
         error("userName", userNameError, 10);
      }
      String passwordError = user.validatePassword(password);
      if (passwordError != null) {
         error("password", passwordError, 9);
      }

      if (userViewError == null) {
         UserProfile loginUser = UserProfile.findByUserName(userbase, userName);
         if (loginUser != null) {
            if (!loginUser.active) {
               priorityError("Account is not active - contact support for assistance");
               return false;
            }
            if (loginUser.salt != null) {
               String salt = loginUser.salt;
               String hashedPassword = UserProfile.getHashedPassword(loginUser, password);
               if (loginUser.password != null && loginUser.password.equals(hashedPassword)) {
                  if (userAuthToken != null)
                     user.deleteAuthToken(user, userAuthToken);

                  user = loginUser;
                  loginStatus = LoginStatus.LoggedIn;

                  userAuthToken = user.createAuthToken();
                  persistAuthToken(userAuthToken);
                  user.loginSuccess(remoteIp);

                  DBUtil.addTestIdInstance(user, "logged-in-as-" + user.userName);
                  return true;
               }
               else
                  loginUser.loginFailed(remoteIp);
            }
         }
         error(null, "No user found with that userName/password", 8);
      }
      return false;
   }

   void logout() {
      loginStatus = LoginStatus.NotLoggedIn;
      clearAuthToken();
      userAuthToken = null;
      if (user != null)
         DynUtil.dispose(user);
      resetUser();
      emailAddress = "";
      userName = "";
      password = "";
      clearErrors();
   }

   boolean register() {
      clearErrors();

      String emailError = user.validateEmailAddress(emailAddress);
      if (emailError != null) {
         error("emailAddress", emailError, 10);
         return false;
      }
      if (userbase.useEmailForUserName)
         userName = emailAddress;
      String userNameError = user.validateUserName(userName);
      if (userNameError != null) {
         error("userName", userNameError, 10);
         return false;
      }
      String passwordError = user.validatePassword(password);
      if (passwordError != null) {
         error("password", passwordError, 10);
         return false;
      }

      String newUserName = StringUtil.isEmpty(userName) ? emailAddress : userName;

      UserProfile existing = UserProfile.findByUserName(userbase, newUserName);
      if (existing != null) {
         priorityError("Account already exists with userName: " + newUserName);
         return false;
      }

      userViewError = null;

      boolean insert = false;

      if (user == null) {
         user = new UserProfile();
         insert = true;

      }
      else if (user.registered) {
         priorityError("User already registered");
         return false;
      }

      propErrors = user.dbValidate();

      if (propErrors == null) {
         user.registered = true;
         user.emailAddress = emailAddress;
         user.userName = newUserName;
         user.salt = DBUtil.createSalt();
         user.password = UserProfile.getHashedPassword(user, password);
      }
      else if (userViewError == null) {
         userViewError = propErrors.toString();
      }

      try {
         if (insert)
            user.dbInsert(false);
         else
            user.dbUpdate();

         userViewStatus = "User " + user.userName + " registered successfully";
      }
      catch (IllegalArgumentException exc) {
         userViewError = "Register failed due to system error";
         DBUtil.error("Register failed: " + exc);
         exc.printStackTrace();
         return false;
      }

      DBUtil.addTestIdInstance(user, "new-registered-user");

      loginStatus = LoginStatus.LoggedIn;

      return true;
   }

   boolean update() {
      userViewStatus = null;
      propErrors = user.dbValidate();
      if (propErrors != null) {
         userViewError = propErrors.toString();
         return false;
      }

      try {
         user.dbUpdate();
      }
      catch (IllegalArgumentException exc) {
         userViewError = "User update failed due to system error";
         DBUtil.error("User update failed: " + exc);
         exc.printStackTrace();
         return false;
      }
      return true;
   }

   void persistAuthToken(String token) {}

   void clearAuthToken(){}

   void removeAddress(Address addr) {
      if (user != null && user.addresses != null) {
         int ix = user.addresses.indexOf(addr);
         if (ix != -1)
            user.addresses.remove(ix);
      }
   }

   void changeHomeAddress(Address addr) {
      if (user != null) {
         user.homeAddress = addr;
      }
   }

   synchronized UserSession getUserSession(SiteContext site) {
      if (sessionMarker == null)
         return null;
      if (userSessions == null) {
         userSessions = new HashMap<Long,UserSession>();
      }
      UserSession session = userSessions.get(site.id);
      if (session == null) {
         session = new UserSession();
         session.user = user;
         session.createTime = new Date();
         session.sessionMarker = sessionMarker;
         userSessions.put(site.id, session);
      }
      return session;
   }

   void addPageEvent(SiteContext site, String pathName) {
      UserSession session = getUserSession(site);
      if (session != null)
         session.addPageEvent(pathName);
   }
}

