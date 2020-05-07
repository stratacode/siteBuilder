@DBTypeSettings
class UserProfile {
   String emailAddress, userName, firstName, lastName, password, salutation;
   boolean emailVerified;

   Address homeAddress;
   List<Address> addresses;

   final static int PRIV_SUPER_ADMIN = 1;
   final static int PRIV_SITE_ADMIN = 2;
   final static int PRIV_REGISTERED = 4;

   final static int MIN_USER_NAME_LEN = 6;

   long userPrivMask = 0;

   // Uses a bidirectional binding to map from boolean to bit in the bit-mask in both directions
   // and for use in queries
   boolean superAdmin :=: (userPrivMask & PRIV_SUPER_ADMIN) != 0;
   boolean siteAdmin :=: (userPrivMask & PRIV_SITE_ADMIN) != 0;
   boolean registered :=: (userPrivMask & PRIV_REGISTERED) != 0;

   UserProfileStats userProfileStats;

   static String validateUserName(String userName) {
      if (userName == null || userName.length() == 0)
         return "Empty user name";
      else if (userName.length() < MIN_USER_NAME_LEN)
         return "User name must be at least " + MIN_USER_NAME_LEN + " characters";
      return null;
   }
}
