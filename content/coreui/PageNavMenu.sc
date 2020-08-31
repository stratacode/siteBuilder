class PageNavMenu extends NavMenu {
   SiteView siteView;

   SiteContext siteContext := siteView.siteContext;
   siteView =: validateSiteView();

   object homeItem extends NavMenuItem {
      url = "/";
      name := siteContext.siteName;
      icon := siteContext.icon;
      url := "/sites/" + siteContext.sitePathName;
   }

   void validateSiteView() {
      //siteContext = siteView == null ? null : siteView.siteContext;
   }

   List<BaseMenuItem> getMenuItems() {
      List<BaseMenuItem> ores = super.getMenuItems();
      List<BaseMenuItem> dres = siteContext.menuItems;
      ArrayList<BaseMenuItem> res = new ArrayList<BaseMenuItem>();
      if (ores != null)
         res.addAll(ores);
      if (dres != null) {
         res.addAll(dres);
      }
      for (int i = 0; i < res.size(); i++)
         res.get(i).listPos = i;
      res.sort(new java.util.Comparator<BaseMenuItem>() {
         public int compare(BaseMenuItem lhs, BaseMenuItem rhs) {
            // First use the configured order value, or if they are equal
            // fall back to sorting by the position in the original list
            if (lhs.orderValue == rhs.orderValue) {
               if (lhs.listPos == rhs.listPos)
                  return 0;
               return lhs.listPos > rhs.listPos ? 1 : -1;
            }
            return lhs.orderValue > rhs.orderValue ? 1 : -1;
         }
      });
      return res;
   }
}
