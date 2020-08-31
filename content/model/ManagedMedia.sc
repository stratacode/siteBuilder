/**
 * Base class for both images and video. It seems like this categorization might make sense in the
 * management UI since adding those two might involve different but overlapping steps.
 */
@DBTypeSettings
@Sync(onDemand=true)
@EditorSettings(displayNameProperty="fileName")
// TODO: this is really an abstract class but we need to instantiate it for the @FindBy because the
// code creates an instance for the prototype. Could find another way to workaround that problem.
// We want to instantiate the object as a prototype so that bindings and other values can be used in the
// match query.
class ManagedMedia extends ManagedResource {
   @FindBy(with="manager")
   String fileName;

   String fileType;

   String suffix;

   String getUrl() {
      return manager.getDisplayUrl(fileName, revision, suffix);
   }

   String captionText;
   String altText;

   // TODO: add visible, alt-resolution, alt-formats - we generate each required res from the closest appropriate source res/format

   String getThumbUrl() {
      return manager.getThumbUrl(fileName, revision, suffix);
   }
   String zoomedUrl;

   int width, height;
   long fileSize;
   byte[] fileHash;

   // Used to store metadata like optionName=optionValue for media specific to a set of options
   String filterPattern;

   @DBPropertySettings(persist=false)
   int mediaChangedCt = 0;

   void mediaChanged() {
      mediaChangedCt++;
   }

   @DBPropertySettings(persist=false)
   String mediaError = null;

   String uniqueFileName := fileName + (revision == null ? "" : MediaManager.RevisionSep + revision);
}
