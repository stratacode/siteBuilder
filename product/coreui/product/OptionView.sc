class OptionView {
   ProductOption option;

   List<OptionValueView> values;

   public void setSelectedIndex(int ix) {
      for (int i = 0; i < values.size(); i++) {
         OptionValueView view = values.get(i);
         boolean newSel = ix == i;
         if (view.selected != newSel)
            view.selected = newSel;
      }
   }

   public int getSelectedIndex() {
      for (int i = 0; i < values.size(); i++) {
         OptionValueView view = values.get(i);
         if (view.selected)
            return i;
      }
      return -1;
   }

   public OptionValueView getSelectedOptionView() {
      for (int i = 0; i < values.size(); i++) {
         OptionValueView view = values.get(i);
         if (view.selected)
            return view;
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