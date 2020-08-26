class NavMenuItem extends BaseMenuItem {
   String getDetailString() {
      return "menu item: " + (name == null || name.length() == 0 ? "..." : name) + (url == null ? " to: ..." : " to: " + url);
   }
}
