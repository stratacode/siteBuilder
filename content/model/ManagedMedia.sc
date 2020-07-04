/**
 * Base class for both images and video. It seems like this categorization might make sense in the
 * management UI since adding those two might involve different but overlapping steps.
 */
@DBTypeSettings
@EditorSettings(displayNameProperty="fileName")
abstract class ManagedMedia extends ManagedResource {
   String fileName;
   String fileType;

   String getUrl() {
      return manager.getDisplayUrl(fileName, fileType);
   }

   String captionText;
   String altText;

   String getThumbUrl() {
      return manager.getThumbUrl(fileName, fileType);
   }
   String zoomedUrl;

   int width, height;
   long fileSize;
   byte[] fileHash;

   int mediaChangedCt = 0;

   void mediaChanged() {
      mediaChangedCt++;
   }

   @DBPropertySettings(persist=false)
   String mediaError = null;
}


