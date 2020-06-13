AddressEditView {
   void updatePostalCode(String postalCode) {
      address.postalCode = postalCode;

      address.validateAddress();
   }
}
