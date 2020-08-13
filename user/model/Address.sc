import sc.dyn.IPropValidator;

@Sync(onDemand=true)
@DBTypeSettings
class Address implements IPropValidator {
   final static int MaxAddressFieldLength = 75;
   String name;
   String address1, address2;
   String city, state, postalCode, country;

   @Bindable
   @DBPropertySettings(persist=false)
   Map<String,String> propErrors = null;

   static String validateAddressField(String value, String fieldName) {
      if (value == null || value.length() == 0)
         return "Missing " + fieldName;
      if (value.length() > 75)
         return fieldName + " is too long";
      return null;
   }

   static String validateName(String name) {
      return validateAddressField(name, "name");
   }

   static String validateAddress1(String addr1) {
      return validateAddressField(addr1, "address");
   }

   static String validateAddress2(String addr2) {
      if (addr2 == null || addr2.length() == 0)
         return null;
      return validateAddressField(addr2, "address2");
   }

   static String validateCity(String city) {
      return validateAddressField(city, "city");
   }

   static String validateState(String state) {
      return validateAddressField(state, "state");
   }

   static String validatePostalCode(String postalCode) {
      return validateAddressField(postalCode, "postalCode");
   }

   static String validateCountry(String country) {
      if (country == null || country.length() == 0) // Use the default
         return null;
      return validateAddressField(country, "country");
   }

   void copyFrom(Address other) {
      name = other.name;
      address1 = other.address1;
      address2 = other.address2;
      city = other.city;
      state = other.state;
      country = other.country;
   }

   void validateAddress() {
      propErrors = DynUtil.validateProperties(this, null);
   }

   // If no fields in the address have been filled out yet, don't display field level errors
   boolean getTreatAsEmpty() {
      return TextUtil.isEmpty(name) && TextUtil.isEmpty(address1);
   }

}
