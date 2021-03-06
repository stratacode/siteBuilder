import sc.util.FileUtil;
import java.io.File;
import java.util.Arrays;
import sc.util.StringUtil;
import java.util.HashSet;
import java.util.HashMap;

MediaManager {
   static HashSet<String> supportedFormats = new HashSet<String>();
   static {
      supportedFormats.add("jpg");
      supportedFormats.add("jpeg");
      supportedFormats.add("png");
   }

   static int maxImageWidth = 4096;
   static int maxImageHeight = 4096;

   boolean refreshMedia(ManagedMedia media) {
      String fileName = media.fileName;
      String fileType = media.fileType;
      String origSuffix = media.suffix;

      String origFileName = getOrigFileName(fileName, origSuffix);
      File origFile = new File(origFileName);

      if (!origFile.canRead()) {
         media.mediaError = "Original media not found";
         return false;
      }

      long size = origFile.length();
      boolean mediaChanged = media.fileSize != size;
      byte[] newHash = FileUtil.computeHash(origFileName);
      if (!mediaChanged) {
         if (media.fileHash == null || !Arrays.equals(media.fileHash, newHash))
            mediaChanged = true;
         if (!mediaChanged && (media.width == 0 || media.height == 0))
            mediaChanged = true;
      }

      if (!mediaChanged) {
         for (int i = 0; i < stdImageWidths.length; i++) {
            int width = stdImageWidths[i];
            String nextFile = getGenFileName(fileName, media.revision, width, fileType);
            if (!mediaChanged) {
               if (!(new File(nextFile)).canRead()) {
                  nextFile = getGenFileName(fileName, media.revision, width, origSuffix);
                  if (!(new File(nextFile)).canRead()) {
                     System.out.println("*** Refresh media: regenerating " + nextFile);
                     mediaChanged = true;
                  }
               }
            }
         }
      }

      if (mediaChanged) {
         // bicycle.png PNG 495x649 495x649+0+0 8-bit sRGB 638084B 0.010u 0:00.011
         String mediaIdOut = FileUtil.exec("magick", "identify", origFileName);
         if (mediaIdOut == null) {
            media.mediaError = "Failed to identify image";
            return false;
         }
         String[] idRes = StringUtil.split(mediaIdOut, ' ');
         String type = idRes[1];

         type = type.toLowerCase();
         if (!supportedFormats.contains(type)) {
            media.mediaError = "Unsupported format: " + type;
            return false;
         }

         String dimsStr = idRes[2];
         int xix = dimsStr.indexOf('x');
         if (xix == -1) {
            System.out.println("*** Invalid format of dimsStr: " + dimsStr);
            media.mediaError = "System error";
            return false;
         }
         String widthStr = dimsStr.substring(0, xix);
         String heightStr = dimsStr.substring(xix+1);
         int mediaWidth = -1;
         int mediaHeight = -1;
         try {
            mediaWidth = Integer.parseInt(widthStr);
            mediaHeight = Integer.parseInt(heightStr);
         }
         catch (NumberFormatException exc) {
            System.out.println("*** Invalid format of dimsStr: " + widthStr + "x" + heightStr);
            media.mediaError = "System error";
            return false;
         }
         if (mediaWidth > maxImageWidth || mediaHeight > maxImageHeight) {
            media.mediaError = "Image dimensions: " + dimsStr + " - larger than max allowed: " + maxImageWidth + "x" + maxImageHeight;
            return false;
         }
         media.width = mediaWidth;
         media.height = mediaHeight;
         media.fileSize = size;
         media.fileHash = newHash;
         media.suffix = origSuffix;

         for (int i = 0; i < stdImageWidths.length; i++) {
            int width = stdImageWidths[i];
            //if (width > media.width) {
            //   continue;
            //}

            String genFileName = getGenFileName(fileName,  null, width, origSuffix);
            String genDir = FileUtil.getParentPath(genFileName);
            if (genDir != null && genDir.length() > 0)
               new File(genDir).mkdirs();

            String cvtRes = FileUtil.exec("convert", origFileName, "-resize", String.valueOf(width), genFileName);
            if (cvtRes == null) {
               System.err.println("*** System err converting image sizes");
               media.mediaError = "System error converting image sizes";
            }
         }
      }
      return mediaChanged;
   }

   private String getOrigFileName(String fileName, String origSuffix) {
      String origDir = mediaStore.origDir;
      return FileUtil.concat(origDir, managerPathName, FileUtil.addExtension(fileName, origSuffix));
   }

   private String getGenFileName(String fileName, String revision, int width, String origSuffix) {
      String genDir = mediaStore.genDir;
      return FileUtil.concat(genDir, managerPathName, getFileNameForSize(fileName, revision, width, origSuffix));
   }

   int removeMediaFiles(String fileName, String suffix) {
      String origFileName = getOrigFileName(fileName, suffix);
      File origFile = new File(origFileName);

      int ct = 0;
      if (origFile.canRead()) {
         if (!origFile.delete())
            System.err.println("*** Unable to delete media file: " + origFile);
         System.out.println("Removed media file: " + origFile);
         ct++;
      }

      for (int i = 0; i < stdImageWidths.length; i++) {
         int width = stdImageWidths[i];

         String genFileName = getGenFileName(fileName, null, width, suffix);
         File genFile = new File(genFileName);
         if (genFile.delete()) {
            System.out.println("*** Removing gen file: " + genFile);
            ct++;
         }
         else
            System.out.println("*** Did not remove gen file: " + genFile);
      }
      return ct;
   }

   void refreshAllMedia() {
      List<ManagedMedia> allMedia = (List<ManagedMedia>) ManagedMedia.getDBTypeDescriptor().findBy(Arrays.asList("manager"), Arrays.asList(this), null, null, 0, 1000);
      if (allMedia != null) {
         for (ManagedMedia media:allMedia)
            media.updateGeneratedFiles();
      }
   }

}
