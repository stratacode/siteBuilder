// Used for a user-defined menu stored in the database
class NavMenuDef extends NavMenu {
   List<BaseMenuItem> subMenuItems;

   subMenuItems =: Bind.sendChangedEvent(this, "detailString");

   @Bindable(manual=true)
   public List<BaseMenuItem> getMenuItems() {
      return subMenuItems;
   }

   @Sync(syncMode=SyncMode.Disabled)
   void setParentMenu(NavMenu parent) {
      if (subMenuItems != null) {
         for (BaseMenuItem baseMenu:subMenuItems)
            baseMenu.setParentMenu(this);
      }
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

   boolean equals(Object other) {
      if (!(other instanceof NavMenuDef))
         return false;
      if (!super.equals(other))
         return false;

      if (!DynUtil.equalObjects(subMenuItems, ((NavMenuDef)other).subMenuItems))
         return false;
      return true;
   }
}