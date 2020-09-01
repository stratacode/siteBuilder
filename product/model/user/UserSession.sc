UserSession {
   void addProductEvent(Product p) {
      addSessionEvent(new ProductEvent(p.id));
   }
   void addCategoryEvent(Category c) {
      addSessionEvent(new CategoryEvent(c.id));
   }
   void addAddToCartEvent(Product p) {
      addSessionEvent(new AddToCartEvent(p.id));
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