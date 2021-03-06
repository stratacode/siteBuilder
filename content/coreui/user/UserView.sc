import java.util.Map;
import java.util.TreeMap;

import sc.util.StringUtil;

@Component
@CompilerSettings(constructorProperties="userbase",compiledOnly=true)
@Sync
class UserView {
   @Sync(syncMode=SyncMode.Disabled)
   Userbase userbase;

   @Sync(initDefault=true)
   String emailAddress = "";
   @Sync(initDefault=true)
   String userName = "";

   String password = "";

   @Sync(syncMode=SyncMode.Disabled)
   String userAuthToken;

   @Sync(syncMode=SyncMode.Disabled)
   String remoteIp;
   @Sync(syncMode=SyncMode.Disabled)
   String acceptLanguage;
   @Sync(syncMode=SyncMode.Disabled)
   String userAgent;

   @Sync(syncMode=SyncMode.Disabled)
   String referrer;

   // Stores the current user profile associated with this view. For anonymous users
   // it will get auto-created to represent the anonymous profile, and source of properties for
   // the register form.
   @Sync(initDefault=true)
   UserProfile user;

   @Sync(syncMode=SyncMode.ServerToClient, initDefault=true)
   LoginStatus loginStatus = LoginStatus.NotLoggedIn;
   // The main error if there are more than one  
   String userViewError;
   String userViewStatus;
   int errorPriority = -1;
   // List of per-property errors
   Map<String,String> propErrors;

   String updatePasswordStatus;

   String updateEmailError;

   String sessionMarker;

   SiteContext lastSite;

   void clearErrors() {
      userViewError = null;
      userViewStatus = null;
      errorPriority = -1;
      propErrors = null;
   }

   void error(String property, String message, int priority) {
      if (userViewError == null || priority  > errorPriority) {
         userViewError = message;
         errorPriority = priority;
      }
      if (property != null) {
         addPropError(property, message);
      }
   }

   void addPropError(String property, String message) {
      if (propErrors == null)
         propErrors = new TreeMap<String,String>();
      propErrors.put(property, message);
   }

   void priorityError(String error) {
      clearErrors();
      userViewError = error;
   }

   String getUserNameDisplay() {
      return !userbase.useEmailForUserName ? "user name" : "email address";
   }

   void changeLastSite(SiteContext site) {
      lastSite = site;
   }
}
