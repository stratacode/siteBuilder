import sc.user.currentUserView;

currentNavMenu {
   boolean loggedIn := currentUserView.loginStatus == LoginStatus.LoggedIn;
   object profileMenu extends NavMenu {
      name := loggedIn ? currentUserView.user.userName : "login";
      icon := loggedIn ? "/icons/user-minus.svg" : "/icons/user-plus.svg";

      object loginMenuItem extends NavMenuItem {
         name = "Login";
         url = "/Login.html";
         visible := !loggedIn;
      }

      object registerMenuItem extends NavMenuItem {
         name = "Register";
         url = "/Register.html";
         visible := !loggedIn;
      }

      object signoutMenuItem extends NavMenuItem {
         name = "Sign out";
         url = "/Logout.html";
         visible := loggedIn;
      }

      object forgetMenuItem extends NavMenuItem {
         name = "Clear session";
         url = "/Logout.html";
      }
   }
}
