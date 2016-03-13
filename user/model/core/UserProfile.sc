class UserProfile {
    String emailAddress, username, firstName, lastName, password, salutation;

    Address homeAddress; 
    List<Address> addresses;

    void login(){
        loginStatus = LoginStatus.LoggedIn;
    }

    void logout(){
        loginStatus = LoginStatus.NotLoggedIn;
    }

    LoginStatus loginStatus = LoginStatus.NotLoggedIn;
}
