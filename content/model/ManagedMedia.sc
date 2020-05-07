/**
 * Base class for both images and video. It seems like this categorization might make sense in the
 * management UI since adding those two might involve different but overlapping steps.
 */
@DBTypeSettings
abstract class ManagedMedia extends ManagedResource {
   String url;
   String captionText;
   String thumbUrl;
   String zoomedUrl;

   int width, height;
   String altText;
}


