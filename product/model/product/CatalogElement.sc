@sc.obj.EditorSettings(displayNameProperty="name")
@CompilerSettings(compiledOnly=true)
abstract class CatalogElement extends ManagedElement {
   @Sync(resetState=true,initDefault=true)
   String shortDesc;

   @sc.obj.HTMLSettings(returnsHTML=true)
   // Don't allow the HTML Property to be set by the client to prevent code injection
   // TODO: we should have a filter attached to HTMLSettings to either make this the default or have a way
   // to add the validate method before setting the property.
   @Sync(syncMode=SyncMode.ServerToClient,initDefault=true)
   String longDesc;

   @Sync(resetState=true,initDefault=true)
   Category parentCategory;

   @DBPropertySettings(indexed=true)
   Storefront store;

   boolean featured;

   override @FindBy(paged=true,orderBy="-lastModified",with="store") pathName;
}
