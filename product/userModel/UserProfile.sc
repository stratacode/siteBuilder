UserProfile {
   PaymentInfo paymentInfo;

   @DBPropertySettings(reverseProperty="storeAdmins")
   List<Storefront> storeList;
}
