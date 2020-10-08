import sc.user.currentUserView;

PageNavMenu {
   boolean loggedIn := currentUserView.loginStatus == LoginStatus.LoggedIn;
   object profileMenu extends NavMenu {
      name := loggedIn ? currentUserView.user.userName : "login";
      icon := loggedIn ? "/icons/user-minus20-grey.svg" : "/icons/user-plus20-grey.svg";

      object loginMenuItem extends NavMenuItem {
         name = "Login";
         url = "/login";
         visible := !loggedIn;
      }

      object registerMenuItem extends NavMenuItem {
         name = "Register";
         url = "/Register.html";
         visible := !loggedIn;
      }

      object editProfileMenuItem extends NavMenuItem {
         name = "Edit profile";
         url = "/profile";
         visible := loggedIn;
      }

      object signoutMenuItem extends NavMenuItem {
         name := loggedIn ? "Sign out" : "Clear session";
         url = "/logout";
      }

   }
}
