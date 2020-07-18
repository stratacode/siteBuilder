@CompilerSettings(liveDynamicTypes=true)
@DBTypeSettings
@Sync(onDemand=true)
class OptionValue {
   String optionValue;
   String skuSymbol;

   public String toString() {
      return optionValue;
   }
}
