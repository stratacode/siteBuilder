UserProfile {
   void orderSubmitted(Order order) {
      if (totalPurchased == null)
         totalPurchased = order.totalPrice;
      else
         totalPurchased = totalPurchased.add(order.totalPrice);
      numOrders++;
   }
}
