/** 
 * One instance for managing a given application's user base. In a multi-tenant program, there is one of
 * these for each tenant but for many applications, there's one per process 
 */
@DBTypeSettings
@Sync(syncMode=SyncMode.Disabled)
class Userbase {
   @FindBy
   @DBPropertySettings(unique=true, required=true, indexed=true)
   String appName;

   String cookieName := appName + "_cookie";

   boolean recordStats = true;
   boolean recordEvents = true;
   boolean useEmailForUserName = true;
   boolean storeAnonymousProfiles = true;
   int maxProfileEvents = 20; // login/logout etc
   int maxSessionEvents = 100; // number of page view and application events for each UserSession
   // 30 days
   int cookieDurationSeconds = 30*24*60*60;
   long authTokenDurationMillis = 30*24*60*60*1000L;

   int passwordMinLen = 6;
   boolean passwordRequireUpperAndLower = false;
   boolean passwordRequireLetterAndDigit = false;
   String passwordIllegalChars = "";

   boolean createAnonymousUser = true;

   boolean secureCookie = false;
   String cookieDomain = null;
   String cookiePath = "/";
}