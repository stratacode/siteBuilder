import sc.lang.html.Window;

NavMenuItem {
   void itemSelected() {
      super.itemSelected();
      Window.window.location.href = url;
   }
}
