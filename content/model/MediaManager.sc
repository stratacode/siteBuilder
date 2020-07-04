@DBTypeSettings
class MediaManager {
   @FindBy(findOne=true)
   String managerPathName;

   String mediaBaseUrl;
   String genBaseUrl;

   int displaySize = 640;
   int thumbSize = 100;
   int zoomSize = 2048;
   int[] stdImageWidths = {100, 640, 1024, 2048};

   String getFileNameForSize(String fileName, int size, String ext) {
      return fileName + "_" + size + "." + ext;
   }

   String getUrl(String fileName, String ext, int size) {
      return genBaseUrl + getFileNameForSize(fileName, size, ext);
   }

   String getDisplayUrl(String fileName, String ext) {
      return getUrl(fileName, ext, displaySize);
   }

   String getThumbUrl(String fileName, String ext) {
      return getUrl(fileName, ext, thumbSize);
   }

   String getZoomUrl(String fileName, String ext) {
      return getUrl(fileName, ext, zoomSize);
   }
}