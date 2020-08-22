// Base class for NavMenuItem and NavMenu
abstract class BaseMenuItem {
   String name;
   String url;
   String icon;
   boolean enabled = true;
   boolean visible = true;
   boolean subMenuVisible = false;

   int selectedCount = 0;
   void itemSelected() {
      if (enabled)
         selectedCount++;
   }

   public List<BaseMenuItem> getMenuItems() {
      return null;
   }

}