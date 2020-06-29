@CompilerSettings(liveDynamicTypes=true)
@DBTypeSettings
@Sync(onDemand=true)
class OptionValue {
   String name;
   String skuPart;

   public String toString() {
      return name;
   }
}
