// Base class for NavMenuItem and NavMenu
abstract class BaseMenuItem {
   String name;
   String url;
   String icon;
   double orderValue = 0.0;

   // State that's changed at runtime - not stored in the DB
   transient boolean enabled = true;
   transient boolean visible = true;
   transient boolean subMenuVisible = false;
   transient int selectedCount = 0;
   transient int listPos;

   void itemSelected() {
      if (enabled)
         selectedCount++;
   }

   public List<BaseMenuItem> getMenuItems() {
      return null;
   }

}