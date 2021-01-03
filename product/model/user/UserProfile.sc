UserProfile {
   @Sync(initDefault=true)
   boolean savePaymentInfo;

   @Sync(initDefault=true)
   PaymentInfo paymentInfo;

   @Sync(initDefault=true)
   int numOrders;

   @Sync(initDefault=true)
   BigDecimal totalPurchased;

   @DBPropertySettings(columnType="jsonb")
   @Sync(initDefault=true)
   List<PaymentInfo> paymentInfos;

   String currencyName;

   String getCurrencySymbol() {
      if (currencyName == null || totalPurchased == null)
         return null;
      Currency c = Currency.getCurrencyForName(currencyName);
      if (c == null)
         return null;
      return c.symbol;
   }
}
