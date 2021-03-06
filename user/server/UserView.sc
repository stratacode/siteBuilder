import java.util.HashMap;
import sc.lang.html.UserAgentInfo;
import sc.lang.html.IWindowEventListener;
import sc.lang.html.Window;

UserView implements IWindowEventListener {
   @Sync(syncMode=SyncMode.Disabled)
   UserAgentInfo userAgentInfo;

   @Sync(syncMode=SyncMode.Disabled)
   Map<Long,UserSession> userSessions;

   void init() {
      if (userAuthToken != null) {
         user = UserProfile.findByAuthToken(userAuthToken);
         if (user == null || user.userbase != userbase)
            userAuthToken = null; // Invalid token
      }
      if (user == null && userName != null && userName.length() > 0 &&
          password != null && password.length() > 0) {
         login();
      }

      initNewUser();

      if (user != null && user.registered) {
         loginStatus = LoginStatus.LoggedIn;
      }
      Window win = Window.window;
      if (win != null)
         win.addEventListener(this);
   }

   void refresh() {
      if (userAuthToken != null) {
         user = UserProfile.findByAuthToken(userAuthToken);
         if (user == null || user.userbase != userbase)
            userAuthToken = null; // Invalid token
      }
      else {
         user = null;
         initNewUser();
      }

      if (user != null && user.registered)
         loginStatus = LoginStatus.LoggedIn;
      else
         loginStatus = LoginStatus.NotLoggedIn;
   }

   void initNewUser() {
      if (user == null && userbase != null && userbase.createAnonUser) {
         resetUser();
         if (userbase.setAnonUserCookie) {
            userAuthToken = user.createAuthToken();
            persistAuthToken(userAuthToken);
         }
      }
   }

   UserProfile initUserForUserName(String userName) {
      UserProfile res = UserProfile.findByUserName(userbase, userName);
      if (res == null) {
         if (user == null || (user.userName != null && !StringUtil.equalStrings(user.userName, userName)))
            resetUser();
         user.userName = userName;
         if (userbase.useEmailForUserName)
            user.emailAddress = userName;
      }
      return user;
   }

   void resetUser() {
      UserProfile oldUser = user;
      user = new UserProfile();
      user.userbase = userbase;
      user.initDefaultFields();

      // Want this to be assigned an id before it gets serialized to the remote side
      // TODO: maybe we should have user = null at this point and only create it on the
      // next request?
      user.dbInsert(false);

      DBUtil.addTestIdInstance(user, "new-anon-profile");

      if (oldUser != null) {
         disposeUser(oldUser);
      }
   }

   void disposeUser(UserProfile oldUser) {
      if (oldUser.userProfileStats != null)
         DynUtil.dispose(oldUser.userProfileStats, true);
      DynUtil.dispose(oldUser, true);
   }

   final static String loginError = "No account with that user name and password";

   boolean login() {
      loginStatus = LoginStatus.NotLoggedIn;

      UserProfile anonUser = user;

      clearErrors();
      String userNameError = user.validateUserName(userName);
      if (userNameError != null) {
         error("userName", loginError, 10);
      }
      String passwordError = user.validatePassword(password);
      if (passwordError != null) {
         error("password", loginError, 9);
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

                  password = "";

                  DBUtil.addTestIdInstance(user, "logged-in-as-" + user.userName);

                  if (anonUser != null)
                     disposeUser(anonUser);

                  return true;
               }
               else
                  loginUser.loginFailed(remoteIp);
            }
         }
         error(null, loginError, 8);
      }
      return false;
   }

   void logout() {
      loginStatus = LoginStatus.NotLoggedIn;
      clearAuthToken();
      userAuthToken = null;
      if (user != null) {
         if (user.userProfileStats != null)
            DynUtil.disposeLater(user.userProfileStats, true);
         DynUtil.disposeLater(user, true);
         user = null;
      }
      // We don't want to create a new user at the end of this request since we are not setting a new cookie
      // and will just create another new one on the start of the next request
      //resetUser();
      emailAddress = "";
      userName = "";
      password = "";
      clearErrors();
      if (userSessions != null)
         userSessions = null;
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
         user.salt = DBUtil.createSalt64();
         user.password = UserProfile.getHashedPassword(user, password);

         user.registerSuccess(remoteIp);
      }
      else if (userViewError == null) {
         userViewError = propErrors.toString();
      }

      if (userViewError != null)
         return false;

      // We sync this in both directions so make sure we clear it out
      password = "";

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
      if (sessionMarker == null || site == null || (userAgentInfo != null && userAgentInfo.isRobot))
         return null;

      if (user == null) {
         if (userbase.createAnonUser)
            initNewUser();
      }
      UserSession session;
      if (userSessions != null) {
         session = userSessions.get(site.id);
         if (session != null)
            return session;
      }

      String userMarker = DBUtil.hashString(userbase.salt, site.sitePathName + ":" + remoteIp + ":" + userAgentInfo.userAgent, true);

      session = userSessionCache.getOrCreateUserSession(userbase, user, userMarker, site);
      if (session == null)
         return null;
      if (userSessions == null)
         userSessions = new TreeMap<Long,UserSession>();
      userSessions.put(site.id, session);
      if (session.createTime == null) {
         session.createTime = new Date();
         session.lastEventTime = session.createTime;
         session.sessionMarker = sessionMarker;
         session.referrer = referrer;
         if (userbase.trackAnonIp || (user != null && user.registered))
            session.remoteIp = remoteIp;
         if (userAgentInfo != null) {
            session.browser = userAgentInfo.browser;
            session.browserVersion = userAgentInfo.browserVersion;
            session.osName = userAgentInfo.osName;
         }
         else
            DBUtil.error("No userAgentInfo for user");
         if (remoteIp != null) {
            GeoIpInfo geoInfo = GeoIpInfo.lookup(remoteIp);
            if (geoInfo != null) {
               CityInfo cityInfo = geoInfo.getCityInfo();
               if (cityInfo != null) {
                  session.countryCode = cityInfo.countryCode;
                  session.cityName = cityInfo.cityName;
                  session.timezone = cityInfo.timezone;
                  if (session.countryCode == null)
                     DBUtil.error("No country found for cityInfo: " + geoInfo.geoNameId + " cityInfoId=" + cityInfo.geoNameId + " city: " + cityInfo.cityName);
               }
               if (session.countryCode == null) {
                  CountryInfo countryInfo = CountryInfo.findByGeoNameId(geoInfo.geoNameId);
                  if (countryInfo != null) {
                     session.countryCode = countryInfo.countryCode;
                     if (session.countryCode == null)
                        DBUtil.error("No country found for countryInfo: " + geoInfo.geoNameId);
                  }
                  else
                     DBUtil.error("No country or city found for geoNameId: " + geoInfo.geoNameId);
               }
               // TODO Do we need this postalCodeInfo?
               //PostalCodeInfo postalCodeInfo = geoInfo.getPostalCodeInfo();
               if (geoInfo.postalCode != null) {
                  session.postalCode = geoInfo.postalCode;
               }
            }
         }
         else if (session.countryCode == null)
            DBUtil.error("No remoteIp for user");
         session.userMarker = userMarker;


         user.getOrCreateStats().numUserSessions++;

         DBUtil.addTestToken(sessionMarker, "session-marker");
      }
      return session;
   }

   void addPageEvent(SiteContext site, String pathName) {
      UserSession session = getUserSession(site);
      if (session != null)
         session.addPageEvent(pathName, 0);
   }

   void updateProfile() {
      // TODO: add validation to the user object and check for propErrors here
      userViewError = null;
      userViewStatus = "Profile updated";
   }

   void updatePassword() {
      updatePasswordStatus = null;
      if (propErrors != null)
         propErrors.remove("password");

      String passwordError = user.validatePassword(password);
      if (passwordError != null) {
         addPropError("password", passwordError);
         return;
      }

      user.password = UserProfile.getHashedPassword(user, password);
      updatePasswordStatus = "Password changed";
      password = "";
   }

   void screenSizeChanged(Window win) {
      if (userSessions != null) {
         for (UserSession session:userSessions.values()) {
            boolean anyChanged = false;
            if (session.sessionEvents != null) {
               for (SessionEvent ev:session.sessionEvents) {
                  if (ev instanceof WindowEvent) {
                     WindowEvent wev = (WindowEvent) ev;
                     if (wev.windowId == win.windowId) {
                        session.screenWidth = win.screen.getWidth();
                        session.screenHeight = win.screen.getHeight();
                        break;
                     }
                  }
               }
            }
            if (anyChanged) {
               sessionChanged(session);
            }
         }
      }
   }

   void windowClosed(Window win, boolean sessionExpired) {
      if (win != null)
         windowClosed(win.windowId, sessionExpired);
   }

   void windowClosed(int winId, boolean sessionExpired) {
      if (userSessions != null) {
         for (UserSession session:userSessions.values()) {
            boolean anyChanged = false;
            if (session.getDBObject().isActive() && session.sessionEvents != null) {
               for (SessionEvent ev:session.sessionEvents) {
                  if (ev instanceof WindowEvent) {
                     WindowEvent wev = (WindowEvent) ev;
                     int evWinId = wev.windowId;
                     if (evWinId != -1 && evWinId == winId) {
                        ev.windowClosed(sessionExpired);
                        anyChanged = true;
                     }
                  }
               }
            }
            if (anyChanged) {
               sessionChanged(session);
            }
         }
         userSessions = null;
      }
   }

   void sessionChanged(UserSession session) {
      // This gets inserted by a scheduled thread so we don't keep resaving the same UserSession and so we can
      // save a bunch of them at a time in a batch update. If we happen to have one here that's been inserted
      // we need to update it.
      if (!session.getDBObject().isTransient()) {
         // We will have updated the duration property in the event and so need the sessionEvents to be recomputed
         session.changedSession = true;
      }
   }

   void updateEmailAddress(String emailAddress) {
      String emailError = user.validateEmailAddress(emailAddress);
      if (emailError != null) {
         addPropError("emailAddress", emailError);
      }
      else {
         if (user.userbase.useEmailForUserName) {
            user.userName = user.emailAddress;
            UserProfile existing = UserProfile.findByUserName(user.userbase, emailAddress);
            if (existing != null && existing != user) {
               addPropError("emailAddress", "Account already exists with userName: " + emailAddress);
               return;
            }
         }
         clearErrors();
         user.emailAddress = emailAddress;
     }
   }
}
