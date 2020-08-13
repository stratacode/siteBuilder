UserProfile {
   boolean savePaymentInfo;

   PaymentInfo paymentInfo;

   @DBPropertySettings(columnType="jsonb")
   List<PaymentInfo> paymentInfos;
}
