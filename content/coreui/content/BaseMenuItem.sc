// Base class for NavMenuItem and NavMenu
@Sync(onDemand=true)
@CompilerSettings(compiledOnly=true)
abstract class BaseMenuItem {
   @Sync(initDefault=true)
   String name;
   @Sync(initDefault=true)
   String url;
   @Sync(initDefault=true)
   String icon;
   @Sync(initDefault=true)
   double orderValue = 0.0;

   // State that's changed at runtime - not stored in the DB
   transient boolean enabled = true;
   transient boolean visible = true;
   transient boolean subMenuVisible = false;
   // Flag to indicate separation between this item and the previous one. For the top-level menu bar, this item
   // begins the right-justified group of items
   transient boolean breakMenuItem = false;
   transient int selectedCount = 0;
   transient int listPos;

   transient NavMenu parentMenu;

   void setParentMenu(NavMenu parent) {
      parentMenu = parent;
   }

   void itemSelected() {
      if (enabled) {
         selectedCount++;
         if (parentMenu != null)
            parentMenu.hideSubMenus();
      }
   }

   @Bindable(manual=true)
   List<BaseMenuItem> getMenuItems() {
      return null;
   }

   void hideSubMenus() {
   }

   name =: Bind.sendChangedEvent(this, "detailString");
   url =: Bind.sendChangedEvent(this, "detailString");

   @Bindable(manual=true)
   abstract String getDetailString();

   void markChanged() {}

   boolean equals(Object other) {
      if (!(other instanceof BaseMenuItem))
         return false;
      BaseMenuItem otherItem = (BaseMenuItem) other;
      if (!DynUtil.equalObjects(otherItem.name, name))
         return false;
      if (!DynUtil.equalObjects(otherItem.url, url))
         return false;
      if (!DynUtil.equalObjects(otherItem.icon, icon))
         return false;
      if (otherItem.orderValue != orderValue)
         return false;
      return true;
   }

   int hashCode() {
      return name == null ? 0 : name.hashCode();
   }

}