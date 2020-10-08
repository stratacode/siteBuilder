import java.util.Collections;

@Component
class NavMenu extends BaseMenuItem {
   void itemSelected() {
      subMenuVisible = !subMenuVisible;
   }

   List<BaseMenuItem> getMenuItems() {
      Object[] arr = DynUtil.getObjChildren(this, null, true);
      if (arr == null)
         return Collections.emptyList();
      ArrayList<BaseMenuItem> res = new ArrayList<BaseMenuItem>(arr.length);
      for (Object elem:arr) {
         if (elem instanceof BaseMenuItem) {
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
}
