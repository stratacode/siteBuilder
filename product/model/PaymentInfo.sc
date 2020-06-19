// TODO: perhaps break out subclass: CreditCardPayment - 
@DBTypeSettings
class PaymentInfo {
   CreditCardType cardType;
   String cardHolder;

   // TODO: This will be stored in a separate data source
   // For the authorizePayment method, we'll have a method in
   // the payment processor that can run on either the client or
   // server that takes this info and returns a token to identify
   // this payment source (or null if it's a one-time deal)
   String cardNumber;
   String token;

   // The x1234 digits or however we display this number to the user
   String last4;

   String cvv;

   String expMonth;
   String expYear;
   String country;

   static final int cardNumberMaxLen = 16;
   static final int cardNumberMinLen = 13;
   static final int blockSize = 4;
   static final int minCvvLen = 3;
   static final int maxCvvLen = 4;

   @DBPropertySettings(persist=false)
   Map<String,String> propErrors = null;

   @Bindable(manual=true)
   String getExpDateString() {
      if (expYear == null || expYear.length() == 0)
         return expMonth;
      String res = expMonth + " / " + expYear;
      System.out.println("*** getExpDateString: " + res);
      return res;
   }

   String validateExpMonth(String newMonth) {
      if (newMonth.length() != 2)
         return "Invalid month format";
      try {
         int month = Integer.parseInt(newMonth);
         if (month <= 0 || month > 12)
            return "Not a valid month";
      }
      catch (NumberFormatException exc) {
         return "Invalid month";
      }
      return checkExpireDate(newMonth, expYear, false);
   }

   String validateExpYear(String newYear) {
      if (newYear.length() != 2)
         return "Invalid year format - expecting last 2 numbers";
      try {
         int year = Integer.parseInt(newYear);
         // TODO: add a check against the current year to be sure the date is in the future?
         return checkExpireDate(expMonth, newYear, true);
      }
      catch (NumberFormatException exc) {
         return "Invalid month";
      }
   }

   private static String checkExpireDate(String expMonth, String expYear, boolean doYear) {
      try {
         int expMonthInt = Integer.parseInt(expMonth);
         int expYearInt = Integer.parseInt(expYear);

         Date now = new Date();
         int nowMonth = now.getMonth() + 1;
         int nowYear = now.getYear() + 1900;
         expYearInt = expYearInt + (nowYear + 10) / 100 * 100;

         if (expYearInt < nowYear && doYear)
            return "Card year expired";
         else if (expYearInt == nowYear && expMonthInt < nowMonth && !doYear)
            return "Card month expired";
      }
      catch (NumberFormatException exc) {
         return "Invalid format";
      }
      return null;
   }

   String validateCardNumber(String number) {
      int len = number.length();
      if (len < cardNumberMinLen)
         return "Card number too short";
      if (len > cardNumberMaxLen)
         return "Card number too long";

      char digit = number.charAt(0);
      switch (digit) {
         case '4': // visa
         case '5': // mc - 51-55
         case '6': // discover
         case '3': // amex - 34, 37, diners club: 300-305, 36, 38, jcb: 35
         case '2': // jcb
         case '1': // jcb
            break;
         default:
            return "Invalid card number prefix";
      }
      int ct = 0;
      for (int i = len-1; i >= 0; i -= 2) {
         digit = number.charAt(i);
         if (!Character.isDigit(digit))
            return "Card number contains non-numbers";
         int digitNum = digit - '0';
         ct += digitNum;
      }
      for (int i = len-2; i >= 0; i -= 2) {
         digit = number.charAt(i);
         if (!Character.isDigit(digit))
            return "Card number contains non-numbers";
         int digitNum = digit - '0';
         digitNum *= 2;
         if (digitNum >= 9)
            digitNum = digitNum / 10 + digitNum % 10;
         ct += digitNum;
      }
      if ((ct % 10) != 0)
         return "Invalid card number";
      return null;
   }


   void updateCardNumber(String value) {
      String newValue;
      int numDigits = 0;
      if (value == null) {
         cardNumber = "";
      }
      else {
         StringBuilder num = new StringBuilder();

         int len = value.length();
         for (int i = 0; i < len; i++) {
            char c = value.charAt(i);
            if (Character.isDigit(c)) {
               num.append(c);
               numDigits++;
            }
         }
         String newNum = num.toString();
         if (newNum.equals(cardNumber)) {
            return;
         }
         else {
            cardNumber = newNum;
            if (numDigits >= cardNumberMinLen && numDigits <= cardNumberMaxLen)
               last4 = "x" + cardNumber.substring(newNum.length() - 4);
         }
      }
      Bind.sendChangedEvent(this, "cardNumberDisplay");
   }

   void updateCvv(String value) {
      if (value == null)
         cvv = "";
      else {
         StringBuilder upd = new StringBuilder();
         for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isDigit(c))
               upd.append(c);

            if (upd.length() == maxCvvLen)
              break;
         }
         cvv = upd.toString();
      }
   }

   String validateCvv(String newVal) {
      int len = newVal == null ? 0 : newVal.length();
      if (len < minCvvLen || len > maxCvvLen)
         return "Invalid security code (cvv)";
      for (int i = 0; i < len; i++) {
         if (!Character.isDigit(newVal.charAt(i)))
            return "Security code must have only numbers";
      }
      return null;
   }

   void updateExpireDate(String value) {
      System.out.println("*** In updateExpireDate: " + value);
      if (value == null) {
         expMonth = "";
         expYear = "";
      }
      else {
         StringBuilder newMonth = new StringBuilder();
         StringBuilder newYear = new StringBuilder();
         boolean slashFound = false;
         for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isDigit(c)) {
               if (newMonth.length() < 2 && !slashFound)
                  newMonth.append(c);
               else if (newYear.length() < 2)
                  newYear.append(c);
            }
            else if (c == '/') {
               slashFound = true;
               if (newMonth.length() < 2) {
                  System.out.println("*** Prepend the zero");
                  newMonth = new StringBuilder("0" + newMonth);
               }
            }
         }
         expMonth = newMonth.toString();
         expYear = newYear.toString();
      }
      Bind.sendChangedEvent(this, "expDateString");
   }

   @Bindable(manual=true)
   String getCardNumberDisplay() {
      StringBuilder disp = new StringBuilder();
      int len = cardNumber == null ? 0 : cardNumber.length();
      for (int i = 0; i < len; i++) {
         if (i != 0 && (i % blockSize) == 0)
            disp.append(" ");
         disp.append(cardNumber.charAt(i));
      }
      if (len > 0 && len < cardNumberMaxLen && (len % blockSize) == 0)
         disp.append(" ");
      return disp.toString();
   }

   String validateCardHolder(String newName) {
      int len = newName == null ? 0 : newName.length();
      if (len < 2 || len > 40)
         return "Invalid card holder name";
      boolean foundAlpha = false;
      boolean foundSpace = false;
      for (int i = 0; i < len; i++) {
         char c = newName.charAt(i);
         if (Character.isLetterOrDigit(c))
            foundAlpha = true;
         else if (c == ' ')
            foundSpace = true;
         else
            return "Invalid letter for name on card";
      }
      if (!foundAlpha)
         return "Name on card must have a letter";
      if (!foundSpace)
         return "Name on card must have at least one space";
      return null;
   }

   void validatePaymentInfo() {
      propErrors = DynUtil.validateProperties(this, null);
   }
}
