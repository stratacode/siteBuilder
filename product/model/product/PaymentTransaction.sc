@DBTypeSettings
class PaymentTransaction {
  PaymentTransactionType type; 

  UserProfile user;
  Order order;
  Date timestamp;

  boolean processed; 
  boolean declined; // set to true if the payment was declined
  String errorCode;
  String errorMessage;
  String token;
}
