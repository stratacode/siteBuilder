UserProfile {
   boolean savePaymentInfo;

   PaymentInfo paymentInfo;

   int numOrders;

   BigDecimal totalPurchased;

   @DBPropertySettings(columnType="jsonb")
   List<PaymentInfo> paymentInfos;
}
