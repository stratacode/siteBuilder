@DBTypeSettings
class BlogLabel {
   @FindBy(with="site")
   @DBPropertySettings(indexed=true)
   String labelName;

   Date lastModified;
   SiteContext site;
}