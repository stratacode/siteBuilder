/* 
 * PaymentProvider

    matches("(51)?(52)?(53)?(54)?(55)?[0-9]{14}")
       return CreditCardType.MASTERCARD;
        }
        if (pan.matches("4[0-9]{15}") || pan.matches("4[0-9]{12}")) {
            return CreditCardType.VISA;
        }
        if (pan.matches("(34)?(37)?[0-9]{13}")) {
            return CreditCardType.AMEX;
        }
        if (pan.matches("(300)?(301)?(302)?(303)?(304)?(305)?[0-9]{11}") || pan.matches("(36)?(38)?[0-9]{12}")) {
            return CreditCardType.DINERSCLUB_CARTEBLANCHE;
        }
        if (pan.matches("6011[0-9]{12}")) {
            return CreditCardType.DISCOVER;
        }
        if (pan.matches("(2014)?(2149)?[0-9]{11}")) {
            return CreditCardType.ENROUTE;
        }
        if (pan.matches("3[0-9]{15}") || pan.matches("(2131)?(1800)?[0-9]{11}")) {
            return CreditCardType.JCB;
        }

   paymentType
   authCode
   requestId
   paymentToken
   declineType - soft/hard

   transactionType - authorize, capture, authorize+capture, settled, refund, void, reverseauth, unconfirmed?

   orderId
   currencyCode
   orderDescription
   subTotal
   shipping
   tax
   totalPrice

   scale for currency as property of currency - how many digits
*/

enum CreditCardType {
   Visa, MasterCard, Amex, DinersClub;

   static CreditCardType fromNumber(String cardNumber) {
      char firstDigit = cardNumber.charAt(0);
      switch (firstDigit) {
         case '4':
            return CreditCardType.Visa;
         case '5':
            return CreditCardType.MasterCard;
         case '3':
            char next = cardNumber.charAt(1);
            switch (next) {
               case '4':
               case '7': // TODO: fix this for logic from above as needed
                 return CreditCardType.Amex;
               case '0':
                 return CreditCardType.DinersClub;
            }
            break;
      }
      return null;
   }
}
