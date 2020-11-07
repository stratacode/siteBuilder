@DBTypeSettings
class MediaManager {
   final static String RevisionSep = "--";
   final static String SizeSep = "_";

   @FindBy(findOne=true)
   String managerPathName;

   String mediaBaseUrl;
   String genBaseUrl;

   int displaySize = 640;
   int minDisplaySize = 320;
   int maxDisplaySize = 640;
   int thumbSize = 100;
   int zoomSize = 2048;
   int[] stdImageWidths = {100, 200, 320, 512, 640, 768, 1024, 2048};

   String getOrigFileName(String fileName, String revision, String ext) {
      return fileName + (revision == null ? "" : RevisionSep + revision) + "." + ext;
   }

   String getFileNameForSize(String fileName, String revision, int size, String ext) {
      return fileName + (revision == null ? "" : RevisionSep + revision) + SizeSep + size + "." + ext;
   }

   String getUrl(String fileName, String revision, String ext, int size) {
      return genBaseUrl + getFileNameForSize(fileName, revision, size, ext);
   }

   String getDisplayUrl(String fileName, String revision, String ext) {
      return getUrl(fileName, revision, ext, displaySize);
   }

   String getDisplaySrcSet(String fileName, String revision, String ext, int nativeRes) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < stdImageWidths.length; i++) {
         int width = stdImageWidths[i];
         if (width < minDisplaySize || width > maxDisplaySize)
            continue;
         if (sb.length() != 0)
            sb.append(",");
         sb.append(getUrl(fileName, revision, ext, width));
         sb.append(" ");
         sb.append(width);
         sb.append("w");
      }
      return sb.toString();
   }

   String getSrcSetForSize(String fileName, String revision, String ext, int size) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < stdImageWidths.length; i++) {
         int width = stdImageWidths[i];
         if (sb.length() != 0)
            sb.append(",");
         sb.append(getUrl(fileName, revision, ext, width));
         sb.append(" ");
         sb.append(width);
         sb.append("w");
         if (width >= size)
            break;
      }
      return sb.toString();
   }

   String getThumbUrl(String fileName, String revision, String ext) {
      return getUrl(fileName, revision, ext, thumbSize);
   }

   String getZoomUrl(String fileName, String revision, String ext) {
      return getUrl(fileName, revision, ext, zoomSize);
   }

   static String removeRevisionFromFile(String uniqueFileName) {
      int ix = uniqueFileName.lastIndexOf(RevisionSep);
      if (ix != -1 && uniqueFileName.length() > ix + 1) {
         return uniqueFileName.substring(0, ix);
      }
      return uniqueFileName;
   }
}