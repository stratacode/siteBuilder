class PageNavMenu extends NavMenu {
   SiteView siteView;
   SiteContext siteContext;

   siteView =: validateSiteView();

   object homeItem extends NavMenuItem {
      url = "/";
      name := siteContext.siteName;
      icon := siteContext.icon;
      url := siteContext.sitePathName;
   }

   void validateSiteView() {
      siteContext = siteView == null ? null : siteView.siteContext;
   }
}
