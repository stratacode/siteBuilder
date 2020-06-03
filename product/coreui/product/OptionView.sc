@Sync
class OptionView {
   @Sync(syncMode=SyncMode.Disabled)
   ProductView productView;

   @Sync(initDefault=true)
   ProductOption option;

   int selectedIndex;

   List<Boolean> enabled;

   public OptionValue getSelectedOption() {
      List<OptionValue> optionValues = option.optionValues;
      if (selectedIndex == -1 || selectedIndex >= optionValues.size())
         return null;
      return optionValues.get(selectedIndex);
   }

   public boolean isEnabled(OptionValue value) {
      if (enabled == null)
         return true;
      int optIx = option.optionValues.indexOf(value);
      if (optIx == -1)
         return false;
      return enabled.get(optIx);
   }

   public String toString() {
      return option == null ? null : option.optionName;
   }
}