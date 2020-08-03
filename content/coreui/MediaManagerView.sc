@Sync
@sc.js.JSSettings(dependentTypes="sc.content.ManagedImage")
@sc.obj.SyncTypeFilter(typeNames={"sc.content.ManagedImage"})
class MediaManagerView {
   MediaManager mediaManager;

   String searchText;

   List<ManagedMedia> currentMedia;

   ManagedMedia selectedMedia;

   boolean showUploadStatus = false;

   boolean uploadError = false;
   String uploadStatusMessage = null;

   String currentMediaStatus;

   void resetUploadForm() {
      showUploadStatus = true;
      uploadError = false;
      uploadStatusMessage = null;
      currentMediaStatus = null;
   }
}
