@CompilerSettings(liveDynamicTypes=true)
@DBTypeSettings
class ProductOption {
   static final String anyStr = "Any";

   String optionName; // e.g. size, color
   @DBPropertySettings(columnType="jsonb")
   List<OptionValue> optionValues; // e.g. large, medium
   OptionValue defaultValue;

   String toString() {
      return optionName;
   }

   @Bindable
   transient List<String> optionFilterList;

   List<String> getOptionFilterList() {
      refreshOptionFilterList();
      return optionFilterList;
   }

   void refreshOptionFilterList() {
      int numOpts = optionValues.size();
      ArrayList<String> res = new ArrayList<String>(numOpts+1);
      res.add(anyString);
      for (int i = 0; i < numOpts; i++) {
         OptionValue optVal = optionValues.get(i);
         if (optVal != null)
            res.add(optVal.optionValue);
         else
            System.err.println("Null option value for productOption: " + optionName);
      }
      if (optionFilterList == null || !optionFilterList.equals(res))
         optionFilterList = res;
   }

   String getAnyString() {
      return anyStr + " " + optionName;
   }
}
