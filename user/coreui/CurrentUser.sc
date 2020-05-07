object CurrentUser {
   String userName = "";
   String password = "";

   UserProfile user;

   LoginStatus loginStatus = LoginStatus.NotLoggedIn;
   String loginError;

   void login() {
       loginStatus = LoginStatus.NotLoggedIn;
       String userNameError = UserProfile.validateUserName(userName);
       if (userNameError != null) {
          loginError = userNameError;
       }
       else {
          user = UserProfile.findByUserPassword(userName, password);
          if (user == null) {
             loginError = "No user with userName/password";
          }
          else
             loginStatus = LoginStatus.LoggedIn;
       }
   }

   void logout() {
       loginStatus = LoginStatus.NotLoggedIn;
   }
}
