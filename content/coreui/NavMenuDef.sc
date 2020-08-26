// Used for a user-defined menu stored in the database
class NavMenuDef extends NavMenu {
   List<BaseMenuItem> subMenuItems;

   subMenuItems =: Bind.sendChangedEvent(this, "detailString");

   public List<BaseMenuItem> getMenuItems() {
      return subMenuItems;
   }

   @Bindable(manual=true)
   String getDetailString() {
      String res = super.getDetailString();
      String next = subMenuItems == null || subMenuItems.size() == 0 ? "(no sub items)" : ": " + getSubItemsDetail();
      return res + next;
   }

   String getSubItemsDetail() {
      StringBuilder sb = new StringBuilder();
      boolean first = true;
      for (BaseMenuItem item: subMenuItems) {
         if (!first)
            sb.append(", ");
         sb.append(item.name);
         first = false;
      }
      return sb.toString();
   }


}