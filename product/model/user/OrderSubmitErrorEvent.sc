class OrderSubmitErrorEvent extends SessionEvent {
   String error;

   OrderSubmitErrorEvent(String e) {
      error = e;
   }
}
