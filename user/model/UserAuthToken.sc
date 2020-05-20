@DBTypeSettings
class UserAuthToken {
   @DBPropertySettings(unique=true, required=true, indexed=true)
   @FindBy
   String token;
   Date createdDate;
   UserProfile user;
   Date expireDate;
}
