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

}