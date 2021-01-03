UserProfile {
   void orderSubmitted(Order order) {
      if (currencyName != null && !DynUtil.equalObjects(order.currencyName, currencyName))
         totalPurchased = null; // TODO: some way to convert the old currency value to the new one?
      if (totalPurchased == null)
         totalPurchased = order.totalPrice;
      else
         totalPurchased = totalPurchased.add(order.totalPrice);
      currencyName = order.currencyName;
      numOrders++;
   }
}
