import sc.db.IDBObject;

import java.util.Arrays;

MediaManagerView {
   static final List<String> searchOrderBy = Arrays.asList("-lastModified");

   static ArrayList<ManagedMedia> searchForText(String text) {
      return new ArrayList<ManagedMedia>((List<ManagedMedia>)ManagedMedia.getDBTypeDescriptor().searchQuery(text, null, null, null, searchOrderBy, -1, -1));
   }

   void doSearch() {
      String txt = searchText == null ? "" : searchText;
      currentMedia = searchForText(txt);
   }

   void clearSearch() {
      resetUploadForm();
      searchText = "";
      currentMedia = null;
   }

   void handleUploadResult(Object res) {
      showUploadStatus = true;
      uploadError = false;
      if (res instanceof Object[])
         res = Arrays.asList((Object[]) res);
      List resList = (List) res;
      if (resList.size() == 1) {
         String nameWithRev = (String) resList.get(0);
         String fileName = stripFileRevision(nameWithRev);
         // Here we want to find all versions of the file uploaded just for context
         currentMedia = searchForText(fileName);
         int selIx = -1;
         for (int i = 0; i < currentMedia.size(); i++) {
            if (currentMedia.get(i).uniqueFileName.equals(nameWithRev)) {
               selectedMedia = currentMedia.get(i);
               selIx = i;
               break;
            }
         }
         if (currentMedia.size() > 1) {
            currentMedia.remove(selIx);
            currentMedia.add(0, selectedMedia);
         }
      }
      else {
         ArrayList<ManagedMedia> newMedia = new ArrayList<ManagedMedia>();
         for (Object resElem:resList) {
            String resText = (String) resElem;
            newMedia.addAll(searchForText(resText));
         }
         currentMedia = newMedia;
         selectedMedia = null;
      }
   }

   static String stripFileRevision(String fn) {
      int ix = fn.indexOf(MediaManager.RevisionSep);
      if (ix == -1)
         return fn;
      return fn.substring(0, ix);
   }

   void handleUploadError(String err) {
      showUploadStatus = true;
      uploadError = true;
      uploadStatusMessage = err;
   }

   void removeMedia(long mediaId) {
      if (mediaId == 0) {
         System.err.println("*** Invalid id in removeMedia: " + mediaId);
         return;
      }
      ManagedMedia media = (ManagedMedia) ManagedMedia.getDBTypeDescriptor().findById(mediaId);
      if (media == null) {
         currentMediaStatus = "Media not found to remove";
         System.err.println("*** removeMedia - media with id: " + mediaId + " not found");
         return;
      }
      resetUploadForm();
      try {
         int toRemIx = currentMedia == null ? -1 : currentMedia.indexOf(media);
         MediaManager manager = media.manager;
         String fileName = media.fileName;
         String fileType = media.fileType;
         media.dbDelete(false);
         manager.removeMediaFiles(fileName, media.suffix);
         if (toRemIx != -1) {
            ArrayList<ManagedMedia> newList = new ArrayList<ManagedMedia>();
            for (int i = 0; i < currentMedia.size(); i++) {
               if (i != toRemIx)
                  newList.add(currentMedia.get(i));
            }
            currentMedia = newList;
         }
         currentMediaStatus = "Removed " + fileName + " " + fileType;
      }
      catch (IllegalArgumentException exc) {
         currentMediaStatus = "Failed to remove media: " + exc;
         return;
      }
   }

}
