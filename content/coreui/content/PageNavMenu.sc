scope<appSession>
class PageNavMenu extends NavMenu {
   @Sync(initDefault=true)
   SiteView siteView;

   SiteContext siteContext := siteView.siteContext;
   siteView =: validateSiteView();

   siteContext =: Bind.sendChangedEvent(this, "menuItems");

   boolean loginVisible = false;

   object homeItem extends NavMenuItem {
      name := siteContext == null ? "StrataCode site builder" : siteContext.siteName;
      icon := siteContext == null ? "/icons/layeredLogo48.png" : siteContext.icon;
      url := siteContext == null ? "/" :  "/" + siteContext.sitePathTypeName + "/" + siteContext.sitePathName;
      orderValue = 0;
   }

   void validateSiteView() {
      //siteContext = siteView == null ? null : siteView.siteContext;
   }

   @Bindable(manual=true)
   List<BaseMenuItem> getMenuItems() {
      List<BaseMenuItem> ores = super.getMenuItems();
      if (siteContext == null)
         return ores;
      List<BaseMenuItem> dres = siteContext.menuItems;
      ArrayList<BaseMenuItem> res = new ArrayList<BaseMenuItem>();
      if (ores != null)
         res.addAll(ores);
      if (dres != null) {
         res.addAll(dres);
      }
      for (int i = 0; i < res.size(); i++) {
         BaseMenuItem elem = res.get(i);
         elem.listPos = i;
         elem.setParentMenu(this);
      }
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

   boolean loggedIn := currentUserView.loginStatus == LoginStatus.LoggedIn;
   object profileMenu extends NavMenu {
      name := loggedIn ? currentUserView.user.userName : "login";
      icon := loggedIn ? "/icons/user-minus20.svg" : "/icons/user-plus20.svg";

      object loginMenuItem extends NavMenuItem {
         name = "Login";
         visible := !loggedIn;
         selectedCount =: loginVisible = !loginVisible;
         //url = "/login";
      }

      object registerMenuItem extends NavMenuItem {
         name = "Register";
         url = "/register";
         visible := !loggedIn;
      }

      object editProfileMenuItem extends NavMenuItem {
         name = "Edit profile";
         url = "/profile";
         visible := loggedIn;
      }

      object signoutMenuItem extends NavMenuItem {
         name := loggedIn ? "Sign out" : "Clear session";
         selectedCount =: currentUserView.logout();
         //url = "/logout";
      }

      orderValue = 8;
   }

   object manageItem extends NavMenuItem {
      //visible := currentUserView.user.siteAdmin;
      visible := siteContext != null && siteContext.isSiteAdmin(currentUserView.user);
      icon := "/icons/settings.svg";
      url := "/manage/site" + (siteContext == null ? "" : "/" + siteContext.sitePathName);
      orderValue = 11;
   }

}
