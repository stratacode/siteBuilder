@DBTypeSettings
class UserProfileStats {
   @DBPropertySettings(reverseProperty="userProfileStats")
   UserProfile userProfile;

   Date registered;
   Date lastLogin;
   Date lastUpdated;
   Date lastPasswordFail;

   int loginCt;
   int passwordFailedCt;

   String remoteIp;

   String[] trustedIps;
   String[] trustedCookies;
}
