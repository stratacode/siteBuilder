UserSession {
   void addProductEvent(Product p) {
      addSessionEvent(new ProductEvent(p));
   }
   void addCategoryEvent(Category c) {
      addSessionEvent(new CategoryEvent(c));
   }
   void addAddToCartEvent(Product p) {
      addSessionEvent(new AddToCartEvent(p));
   }
   void addCheckoutStartEvent() {
      addSessionEvent(new CheckoutStartEvent());
   }
   void addOrderSubmitEvent() {
      addSessionEvent(new OrderSubmitEvent());
   }
   void addOrderSubmitErrorEvent(String error) {
      addSessionEvent(new OrderSubmitErrorEvent(error));
   }
}