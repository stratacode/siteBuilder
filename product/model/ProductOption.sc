@CompilerSettings(liveDynamicTypes=true)
@DBTypeSettings
class ProductOption {
   String optionName; // e.g. size, color
   @DBPropertySettings(columnType="jsonb")
   List<OptionValue> optionValues; // e.g. large, medium
   OptionValue defaultValue;

   public String toString() {
      return optionName;
   }
}
