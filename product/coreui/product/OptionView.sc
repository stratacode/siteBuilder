@Sync
class OptionView {
   @Sync(initDefault=true)
   ProductOption option;

   @Sync(initDefault=true)
   List<OptionValueView> values;

   public void setSelectedIndex(int ix) {
      if (values != null) {
         for (int i = 0; i < values.size(); i++) {
            OptionValueView view = values.get(i);
            boolean newSel = ix == i;
            if (view.selected != newSel)
               view.selected = newSel;
         }
      }
      else if (ix != -1)
         System.err.println("*** setSelectedIndex - with no values");
   }

   public int getSelectedIndex() {
      if (values != null) {
         for (int i = 0; i < values.size(); i++) {
            OptionValueView view = values.get(i);
            if (view.selected)
               return i;
         }
      }
      return -1;
   }

   public OptionValueView getSelectedOptionView() {
      if (values != null) {
         for (int i = 0; i < values.size(); i++) {
            OptionValueView view = values.get(i);
            if (view.selected)
               return view;
         }
      }
      return null;
   }

   public OptionValue getSelectedOption() {
      OptionValueView selView = getSelectedOptionView();
      return selView == null ? null : selView.value;
   }

   public String toString() {
      return option == null ? null : option.optionName;
   }
}