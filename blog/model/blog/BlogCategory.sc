@DBTypeSettings
class BlogCategory extends BlogElement {
   override @DBPropertySettings(reverseProperty="subCategories") parentCategory;

   @Sync(onDemand=true, initDefault=true)
   List<BlogPost> childPosts;

   @Sync(onDemand=true, initDefault=true)
   @DBPropertySettings(columnType="jsonb")
   List<BlogPost> linkedPosts;

   @Sync(onDemand=true, initDefault=true)
   List<BlogCategory> subCategories;

   @DBPropertySettings(columnType="jsonb")
   @Sync(syncMode=SyncMode.Disabled)
   Query postQuery;

   @DBPropertySettings(persist=false)
   @Sync(initDefault=true)
   List<BlogPost> allPosts;

   String pageUrl := "/" + site.sitePathTypeName + "/" + site.sitePathName + "/blogCategory/" + pathName;

   override @FindBy(paged=true,orderBy="-lastModified",with="site") pathName;
}
