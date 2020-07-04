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

   static HashMap<String,String> typeToSuffix = new HashMap<String,String>();
   static {
      typeToSuffix.put("jpeg", "jpg");
   }

   static int maxImageWidth = 4096;
   static int maxImageHeight = 4096;

   void refreshMedia(ManagedMedia media) {
      String fileName = media.fileName;
      String fileType = media.fileType;

      String origDir = mediaStore.origDir;
      String genDir = mediaStore.genDir;

      String origFileName = FileUtil.concat(origDir, FileUtil.addExtension(fileName, fileType));
      File origFile = new File(origFileName);

      if (!origFile.canRead()) {
         media.mediaError = "Original media not found";
         return;
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
            if (width > media.width)
               continue;
            String nextFile = FileUtil.concat(genDir, getFileNameForSize(fileName, width, fileType));
            if (!mediaChanged) {
               if (!(new File(nextFile)).canRead()) {
                  System.out.println("*** Missing generated file for: " + width + ":" + nextFile);;
                  mediaChanged = true;
               }
            }
         }
      }

      if (mediaChanged) {
         // bicycle.png PNG 495x649 495x649+0+0 8-bit sRGB 638084B 0.010u 0:00.011
         String mediaIdOut = FileUtil.exec("magick", "identify", origFileName);
         if (mediaIdOut == null) {
            media.mediaError = "Failed to identify image";
            return;
         }
         String[] idRes = StringUtil.split(mediaIdOut, ' ');
         String type = idRes[1];

         type = type.toLowerCase();
         if (!supportedFormats.contains(type)) {
            media.mediaError = "Unsupported format: " + type;
            return;
         }

         String suffix = typeToSuffix.get(type);
         if (suffix == null)
            suffix = type;

         if (!suffix.equals(fileType)) {
            System.out.println("*** Changing file type - extension: " + fileType + " format of file: " + type);
            media.fileType = suffix;
         }

         String dimsStr = idRes[2];
         int xix = dimsStr.indexOf('x');
         if (xix == -1) {
            System.out.println("*** Invalid format of dimsStr: " + dimsStr);
            media.mediaError = "System error";
            return;
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
            return;
         }
         if (mediaWidth > maxImageWidth || mediaHeight > maxImageHeight) {
            media.mediaError = "Image dimensions: " + dimsStr + " - larger than max allowed: " + maxImageWidth + "x" + maxImageHeight;
            return;
         }
         media.width = mediaWidth;
         media.height = mediaHeight;

         for (int i = 0; i < stdImageWidths.length; i++) {
            int width = stdImageWidths[i];
            if (width > media.width)
               continue;

            String genFileName = FileUtil.concat(genDir, getFileNameForSize(fileName, width, fileType));
            String cvtRes = FileUtil.exec("convert", origFileName, "-resize", String.valueOf(width), genFileName);
            if (cvtRes == null) {
               media.mediaError = "System error converting image sizes";
            }
         }

      }
   }
}
