import sc.lang.html.Window;

NavMenuItem {
   void itemSelected() {
      super.itemSelected();
      if (url != null)
         Window.window.location.href = url;
   }
}
