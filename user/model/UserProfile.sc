import java.util.UUID;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@DBTypeSettings
@FindBy(name="userNamePassword", with="userbase,userName,password",findOne=true)
@FindBy(name="userName", with="userbase,userName",findOne=true)
@Sync(onDemand=true)
class UserProfile {
   @Sync(syncMode=SyncMode.Disabled)
   @DBPropertySettings(required=true)
   /** Separate Userbase's will be separate collections of user objects, perhaps shared or separate for different sites */
   Userbase userbase;

   @Sync(initDefault=true)
   @DBPropertySettings(indexed=true)
   String emailAddress;

   @Sync(initDefault=true)
   @DBPropertySettings(indexed=true)
   String userName;

   @Sync(syncMode=SyncMode.ClientToServer)
   String password;
   @Sync(syncMode=SyncMode.Disabled)
   String salt;

   @Sync(initDefault=true)
   String firstName, lastName, salutation;
   @Sync(syncMode=SyncMode.ServerToClient, initDefault=true)
   boolean emailVerified;

   @Sync(syncMode=SyncMode.ServerToClient, initDefault=true)
   Date lastModified;

   @Sync(syncMode=SyncMode.ServerToClient, initDefault=true)
   boolean locked;
   @Sync(syncMode=SyncMode.ServerToClient, initDefault=true)
   boolean limitExceeded;
   @Sync(syncMode=SyncMode.ServerToClient, initDefault=true)
   Date limitRestoreDate;

   @Sync(initDefault=true)
   Address homeAddress;

   @DBPropertySettings(columnType="jsonb")
   @Sync(initDefault=true)
   List<Address> addresses;

   final static int PRIV_SUPER_ADMIN = 1;
   final static int PRIV_SITE_ADMIN = 2;
   final static int PRIV_REGISTERED = 4;

   final static int MIN_USER_NAME_LEN = 6;

   @Sync(syncMode=SyncMode.ServerToClient, initDefault=true)
   long userPrivMask = 0;

   // Uses a bidirectional binding to map from boolean to bit in the bit-mask in both directions
   // and for use in queries
   boolean superAdmin :=: (userPrivMask & PRIV_SUPER_ADMIN) != 0;
   boolean siteAdmin :=: (userPrivMask & PRIV_SITE_ADMIN) != 0;
   boolean registered :=: (userPrivMask & PRIV_REGISTERED) != 0;

   // TODO: add a role constraint here so this is only visible by site-admins in the manager UI - it's not needed when
   // the UserProfile is used in the main storefront
   @Sync(syncMode=SyncMode.ServerToClient,initDefault=true)
   UserProfileStats userProfileStats;

   private transient Locale locale;

   String localeTag; // en_US, etc

   String validateUserName(String userName) {
      if (userName == null || userName.length() == 0)
         return "Empty user name";
      else if (userName.length() < MIN_USER_NAME_LEN)
         return "User name must be at least " + MIN_USER_NAME_LEN + " characters";
      return null;
   }

   String validateEmailAddress(String emailAddress) {
      return TextUtil.validateEmailAddress(emailAddress);
   }

   String validatePassword(String password) {
      if (password == null || password.length() == 0)
         return "Empty password";
      boolean foundUpper = false;
      boolean foundLower = false;
      boolean foundSpecial = false;
      boolean foundDigit = false;
      boolean foundLetter = false;
      boolean foundIllegal = false;
      int len = password.length();
      if (len < userbase.passwordMinLen)
         return "Password must have at least " + userbase.passwordMinLen + " characters";
      for (int i = 0; i < len; i++) {
         char cur = password.charAt(i);
         if (!foundUpper && Character.isUpperCase(cur))
            foundUpper = true;
         else if (!foundLower && Character.isLowerCase(cur))
            foundLower = true;
         else if (Character.isDigit(cur))
            foundDigit = true;
         else if (Character.isLetter(cur))
            foundLetter = true;
         else {
            if (userbase.passwordIllegalChars.indexOf(cur) != -1)
               foundIllegal = true;
            foundSpecial = true;
         }
      }
      if (userbase.passwordRequireUpperAndLower && (!foundUpper || !foundLower))
         return "Password must contain at least one upper and one lower case character";
      if (userbase.passwordRequireLetterAndDigit && (!foundDigit || !foundLetter))
         return "Password must contain at least one number and one letter";
      if (foundIllegal)
         return "Password must not contain one of: " + userbase.passwordIllegalChars;
      return null;
   }

   public boolean isActive() {
      if (locked)
         return false;
      if (limitExceeded) {
         Date now = new Date();
         if (limitRestoreDate != null && now.after(limitRestoreDate)) {
            limitExceeded = false;
            limitRestoreDate = null;
         }
         else
            return false;
      }
      return true;
   }

   void initDefaultFields() {
      if (firstName == null)
         firstName = "";
      if (lastName == null)
         lastName = "";
      if (emailAddress == null)
         emailAddress = "";
   }

   String getName() {
      if (firstName != null && firstName.length() > 0 && lastName != null && lastName.length() > 0)
         return firstName + " " + lastName;
      return null;
   }

   String getDisplayName() {
      String name = getName();
      if (name != null)
         return name;
      return userName;
   }

}
