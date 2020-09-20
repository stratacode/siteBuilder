import sc.util.FileUtil;
import java.io.File;
import java.util.Arrays;
import sc.util.StringUtil;
import java.util.HashSet;
import java.util.HashMap;
class MediaInfo {
   int width, height;
   long fileSize;
   String fileType;
   String suffix;
   String error;

   static MediaInfo getFromFile(String fileName) {
   // bicycle.png PNG 495x649 495x649+0+0 8-bit sRGB 638084B 0.010u 0:00.011
      String mediaIdOut = FileUtil.exec("magick", "identify", fileName);
      if (mediaIdOut == null) {
         return null;
      }
      String[] idRes = StringUtil.split(mediaIdOut, ' ');
      String type = idRes[1];

      String origSuffix = FileUtil.getExtension(fileName);

      MediaInfo info = new MediaInfo();

      type = type.toLowerCase();

      info.fileType = type;
      if (!MediaManager.supportedFormats.contains(type)) {
         info.error = "Unsupported format: " + type;
         return info;
      }

      info.suffix = origSuffix;

      String dimsStr = idRes[2];
      int xix = dimsStr.indexOf('x');
      if (xix == -1) {
         System.out.println("*** Invalid format of dimsStr: " + dimsStr);
         info.error = "System error - dims str format";
         return info;
      }
      String widthStr = dimsStr.substring(0, xix);
      String heightStr = dimsStr.substring(xix+1);
      int mediaWidth = -1;
      int mediaHeight = -1;
      try {
         info.width = Integer.parseInt(widthStr);
         info.height = Integer.parseInt(heightStr);
      }
      catch (NumberFormatException exc) {
         System.out.println("*** Invalid format of dimsStr: " + widthStr + "x" + heightStr);
         info.error = "System error";
      }
      return info;
   }
}