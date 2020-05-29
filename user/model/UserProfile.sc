import java.util.UUID;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@DBTypeSettings
@FindBy(name="userNamePassword", with="mgr,userName,password",findOne=true)
@FindBy(name="userName", with="mgr,userName",findOne=true)
class UserProfile {
   @DBPropertySettings(required=true)
   UserManager mgr;

   @DBPropertySettings(indexed=true)
   String emailAddress; 

   @DBPropertySettings(indexed=true)
   String userName;
   String password;
   String salt;

   String firstName, lastName, salutation;
   boolean emailVerified;

   Date lastModified;

   boolean locked;
   boolean limitExceeded;
   Date limitRestoreDate;

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

   private transient Locale locale;

   String localeTag; // en_US, etc

   String validateUserName(String userName) {
      if (userName == null || userName.length() == 0)
         return "Empty user name";
      else if (userName.length() < MIN_USER_NAME_LEN)
         return "User name must be at least " + MIN_USER_NAME_LEN + " characters";
      return null;
   }

   private final static String allowedEmailChars = "+_!%#$&'*/=^`{}|~;";

   String validateEmailAddress(String emailAddress) {
      if (emailAddress == null || emailAddress.length() == 0)
         return "Empty email address";
      if (emailAddress.length() > 256)
         return "Email address is too long";

      int atIx = emailAddress.indexOf('@');
      if (atIx == -1 || emailAddress.length() - atIx < 5)
         return "Email address invalid";
      String local = emailAddress.substring(0, atIx);
      String domain = emailAddress.substring(atIx+1);
      int lastDot = -1;
      int i;
      int localLen = local.length();
      boolean quoted = false;
      for (i = 0; i < localLen; i++) {
         char l = local.charAt(i);
         if (l == '.') {
            if (i == 0 || i == localLen - 1)
               break;
            if (lastDot == i - 1)
               break;
            lastDot = l;
         }
         else if (l == '-') {
            if (i == 0 || i == localLen - 1)
               break;
         }
         else if (l == '"') {
            if (i == 0)
               quoted = true;
            else if (quoted && l != localLen - 1)
               break;
         }
         else if (Character.isWhitespace(l)) {
            if (!quoted)
              break;
         }
         else if (!Character.isLetterOrDigit(l)) {
            if (allowedEmailChars.indexOf(l) == -1)
               break;
         }
      }
      if (i != localLen) 
         return "Invalid email name: expected name@server.domain";

      int domainLen = domain.length();
      boolean foundDot = false;
      for (i = 0; i < domainLen; i++) {
         char d = domain.charAt(i);
         if (d == '.') {
            if (i == domainLen - 1)
               break;
            foundDot = true;
         }
         else if (!Character.isLetterOrDigit(d) && d != '-')
            break;
      }
      if (i != domainLen || !foundDot) 
         return "Invalid email server: expected name@server.domain";
      return null;
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
      if (len < mgr.passwordMinLen)
         return "Password must have at least " + mgr.passwordMinLen + " characters";
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
            if (mgr.passwordIllegalChars.indexOf(cur) != -1)
               foundIllegal = true;
            foundSpecial = true;
         }
      }
      if (mgr.passwordRequireUpperAndLower && (!foundUpper || !foundLower))
         return "Password must contain at least one upper and one lower case character";
      if (mgr.passwordRequireLetterAndDigit && (!foundDigit || !foundLetter))
         return "Password must contain at least one number and one letter";
      if (foundIllegal)
         return "Password must not contain one of: " + mgr.passwordIllegalChars;
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
}
