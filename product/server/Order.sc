Order {
   String validateForCheckout() {
      // Add rules like in-stock checks to be run before we allow them to begin the checkout process
      return null;
   }

   String validateForSubmit() {
      // Add rules for validating payment, address, etc. Per-property validation will have already been performed.
      return null;
   }
}