Storefront {
   String makeOrderNumber(Order order) {
      return orderPrefix + order.id;
   }

}