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
   boolean trackAnonIp = true; // Track the remoteIp of anonymous users
   int maxProfileEvents = 20; // login/logout etc
   int maxSessionEvents = 100; // number of page view and application events for each UserSession
   // 30 days
   int cookieDurationSeconds = 30*24*60*60;
   long authTokenDurationMillis = 30*24*60*60*1000L;

   int passwordMinLen = 6;
   boolean passwordRequireUpperAndLower = false;
   boolean passwordRequireLetterAndDigit = false;
   String passwordIllegalChars = "";

   /**
    * Should we create a UserProfile for a non-robot website user that has not registered. Currently a UserProfile
    * is required for event tracking
    */
   boolean createAnonUser = true;
   /** Should we set a cookie to track an anonymous user. If createAnonUser is true, userMarker is used to track them */
   boolean setAnonUserCookie = true;

   boolean secureCookie = false;
   String cookieDomain = null;
   String cookiePath = "/";

   // Used for userbase-wide salting of hashed values to avoid possible rainbow attach to deanonymize users
   byte[] salt;
}
