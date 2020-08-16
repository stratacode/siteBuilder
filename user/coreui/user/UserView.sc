import java.util.Map;
import java.util.TreeMap;

import sc.util.StringUtil;

@Component
@CompilerSettings(constructorProperties="mgr")
class UserView {
   @Sync(syncMode=SyncMode.Disabled)
   UserManager mgr;

   String emailAddress = "";
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

   // Stores the current user profile associated with this view. For anonymous users
   // it will get auto-created to represent the anonymous profile, and source of properties for
   // the register form.
   UserProfile user;

   @Sync(syncMode=SyncMode.ServerToClient)
   LoginStatus loginStatus = LoginStatus.NotLoggedIn;
   // The main error if there are more than one  
   String userViewError;
   String userViewStatus;
   int errorPriority = -1;
   // List of per-property errors
   Map<String,String> propErrors;

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
         if (propErrors == null)
            propErrors = new TreeMap<String,String>();
         propErrors.put(property, message);
      }
   }

   void priorityError(String error) {
      clearErrors();
      userViewError = error;
   }

   String getUserNameDisplay() {
      return !mgr.useEmailForUserName ? "user name" : "email address";
   }
}
