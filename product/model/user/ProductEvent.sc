class ProductEvent extends SessionEvent {
   long productId;
   ProductEvent(long pId) {
      productId = pId;
   }
}
