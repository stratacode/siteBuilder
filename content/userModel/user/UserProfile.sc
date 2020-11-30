UserProfile {
   @Sync(initDefault=true,syncMode=SyncMode.ServerToClient)
   @DBPropertySettings(reverseProperty="siteAdmins")
   List<SiteContext> siteList;
}
