/**
 * This is the basic blog post object
 */
@Sync(onDemand=true)
@DBTypeSettings
class BlogPost extends BlogElement {
   UserProfile author;

   @sc.obj.HTMLSettings(returnsHTML=true)
   // Don't allow the HTML Property to be set by the client to prevent code injection
   // TODO: we should have a filter attached to HTMLSettings to either make this the default or have a way
   // to add the validate method before setting the property.
   @Sync(syncMode=SyncMode.ServerToClient,initDefault=true)
   String postContent;

   Date postDate;

   // Could be in a promotions layer but also pretty basic so might be best left in the core model but enabled in various layer configurations.
   @DBPropertySettings(columnType="jsonb")
   List<BlogLabel> labels;

   override @FindBy(paged=true,orderBy="-lastModified",with="site") pathName;

   override @DBPropertySettings(reverseProperty="childPosts") parentCategory;

   String pageUrl := "/" + site.sitePathTypeName + "/" + site.sitePathName + "/post/" + pathName;
}
