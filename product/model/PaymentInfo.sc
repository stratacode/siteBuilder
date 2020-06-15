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
}
