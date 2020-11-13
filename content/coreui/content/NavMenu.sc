import java.util.Collections;

@Component
@sc.obj.SyncTypeFilter(typeNames={"sc.content.NavMenuDef"})
@sc.js.JSSettings(dependentTypes="sc.content.NavMenuDef")
@CompilerSettings(compiledOnly=true)
class NavMenu extends BaseMenuItem {
   // When not null, specifies an order value to separate menuItems between left and right groups
   Double breakOrderValue;

   void itemSelected() {
      subMenuVisible = !subMenuVisible;
   }

   @Bindable(manual=true)
   List<BaseMenuItem> getMenuItems() {
      Object[] arr = DynUtil.getObjChildren(this, null, true);
      if (arr == null)
         return Collections.emptyList();
      ArrayList<BaseMenuItem> res = new ArrayList<BaseMenuItem>(arr.length);
      boolean breakFound = false;
      for (Object elem:arr) {
         if (elem instanceof BaseMenuItem) {
            BaseMenuItem menuItem = (BaseMenuItem) elem;
            menuItem.parentMenu = this;
            if (!breakFound && breakOrderValue != null && breakOrderValue < menuItem.orderValue) {
               menuItem.breakMenuItem = true;
               breakFound = true;
            }
            else
               menuItem.breakMenuItem = false;
            res.add((BaseMenuItem) elem);
         }
      }
      return res;
   }

   void hideSubMenus() {
      List<BaseMenuItem> menuItems = getMenuItems();
      subMenuVisible = false;
      if (menuItems != null) {
         for (BaseMenuItem subMenu:menuItems) {
            subMenu.hideSubMenus();
         }
      }
   }

   String getDetailString() {
      return "nav menu " + (name == null || name.length() == 0 ? "..." : name) +
                       (url != null && url.length() > 0 ? " to: " + url : "");
   }

   void markChanged() {
      Bind.sendChangedEvent(this, "menuItems");
   }

}
