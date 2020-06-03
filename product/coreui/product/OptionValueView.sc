@Sync
class OptionValueView {
   ProductView productView;
   OptionValue value;
   boolean enabled, selected;

   public String toString() {
      return value == null ? "" : value.name;
   }
}
