class OptionValueView {
   ProductView productView;
   OptionValue value;
   boolean enabled, selected;

   selected =: productView.invalidateOptions();

   public String toString() {
      return value == null ? "" : value.name;
   }
}
