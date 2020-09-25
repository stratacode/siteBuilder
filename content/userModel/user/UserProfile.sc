UserProfile {
   @Sync(initDefault=true)
   @DBPropertySettings(reverseProperty="siteAdmins")
   List<SiteContext> siteList;
}
