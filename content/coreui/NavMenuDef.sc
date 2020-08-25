// Used for a user-defined menu stored in the database
class NavMenuDef extends NavMenu {
   List<BaseMenuItem> subMenuItems;

   public List<BaseMenuItem> getMenuItems() {
      return subMenuItems;
   }
}