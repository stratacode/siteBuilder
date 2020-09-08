UserManager {
   /** Merges together both the current cart along with the submitted orders for the UI */
   List<Order> getOrdersForUser(UserProfile user) {
      if (user == null)
         return null;
      Order pending = OrderView.getPendingOrderForUser(user, store);
      List<Order> res;
      // Don't include empty pending orders since there's nothing in the cart in this case
      if (pending != null && pending.totalPrice != null && pending.totalPrice.compareTo(BigDecimal.ZERO) > 0) {
         res = new BArrayList<Order>();
         res.add(pending);
      }
      else
         res = null;
      List<Order> submitted = OrderView.getSubmittedOrdersForUser(user, store);
      if (submitted != null && submitted.size() > 0) {
         if (res == null)
            res = submitted;
         else {
            res.addAll(submitted);
         }
      }
      return res;
   }
}