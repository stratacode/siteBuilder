@Sync
@sc.js.JSSettings(dependentTypes="sc.content.ManagedImage")
@sc.obj.SyncTypeFilter(typeNames={"sc.content.ManagedImage"})
class MediaManagerView {
   @Sync(syncMode=SyncMode.Disabled)
   MediaManager mediaManager;

   // This is synchronized so the file upload servlet can find the right media manager without having to serialize the MediaManager
   String mediaManagerId;

   SiteContext site;

   String searchText;

   List<ManagedMedia> currentMedia;

   ManagedMedia selectedMedia;

   boolean showUploadStatus = false;

   boolean uploadError = false;
   String uploadStatusMessage = null;
   String searchStatusMessage = null;

   String currentMediaStatus;
   String currentMediaError;

   String resError;

   void resetUploadForm() {
      showUploadStatus = true;
      uploadError = false;
      uploadStatusMessage = null;
      currentMediaStatus = null;
      currentMediaError = null;
      searchStatusMessage = null;
   }

   override @Exec(serverOnly=true)
   mediaManager := site.mediaManager;
}
