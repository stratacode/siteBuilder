class AddToCartEvent extends SessionEvent {
   long productId;
   AddToCartEvent(long id) {
      productId = id;
   }
}
