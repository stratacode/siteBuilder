@Sync
@sc.js.JSSettings(dependentTypes="sc.content.ManagedImage")
@sc.obj.SyncTypeFilter(typeNames={"sc.content.ManagedImage"})
class MediaManagerView {
   MediaManager mediaManager;

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
