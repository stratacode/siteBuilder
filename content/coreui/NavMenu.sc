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
}