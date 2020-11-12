@CompilerSettings(compiledOnly=true)
abstract class BlogElement extends ManagedElement {
   @Sync(resetState=true,initDefault=true)
   BlogCategory parentCategory;

   @DBPropertySettings(indexed=true)
   SiteContext site;

   @Sync(resetState=true,initDefault=true)
   String shortDesc;

   @sc.obj.HTMLSettings(returnsHTML=true)
   // Don't allow the HTML Property to be set by the client to prevent code injection
   // TODO: we should have a filter attached to HTMLSettings to either make this the default or have a way
   // to add the validate method before setting the property.
   @Sync(syncMode=SyncMode.ServerToClient,initDefault=true)
   String longDesc;

   override @FindBy(paged=true,orderBy="-lastModified",with="site") pathName;
}
