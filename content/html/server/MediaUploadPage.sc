import java.util.Map;

import sc.servlet.UploadResult;
import sc.util.FileUtil;

import java.io.File;

@URL(pattern="/mediaUpload", mimeType="text/plain")
scope<request>
class MediaUploadPage extends UploadPage {
   uploadPath = "/tmp/imageUpload";

   MediaManager manager;

   UploadResult processUpload(Map<String,String> uploadedFiles, Map<String,String> formFields) {
      MediaManager useManager = manager;
      if (useManager == null) {
         String midStr = formFields.get("mediaManagerId");
         if (midStr != null) {
            try {
               long mid = Long.parseLong(midStr);
               useManager = (MediaManager) MediaManager.getDBTypeDescriptor().findById(mid);
            }
            catch (NumberFormatException exc) {
            }
         }

      }
      if (useManager == null) {
         System.err.println("*** Form error - no manager configured for MediaUploadPage");
         return new UploadResult(null, "No media manager configured");
      }

      List<String> errors = new ArrayList<String>();
      List<String> newFiles = new ArrayList<String>();

      for (Map.Entry<String,String> ent:uploadedFiles.entrySet()) {
         String fileNameWithExt = ent.getValue();
         String uploadFileName = FileUtil.concat(uploadPath, fileNameWithExt);
         String fileName = FileUtil.removeExtension(fileNameWithExt);

         MediaInfo info = MediaInfo.getFromFile(uploadFileName);
         if (info == null) {
            System.out.println("Uploaded file: " + ent.getKey() + ": " + ent.getValue() + " - no MediaInfo in file");
            errors.add("No MediaInfo in file");
            break;
         }
         if (info.error != null) {
            errors.add(info.error);
         }

         // Use standard file extension
         fileNameWithExt = FileUtil.addExtension(fileName, info.fileType);

         String revision = null;

         List<ManagedMedia> oldMedia = ManagedMedia.findByFileName(fileName);
         String origFileName = FileUtil.concat(mediaStore.origDir, fileNameWithExt);
         File orig = new File(origFileName);
         boolean found = false;
         byte[] origHash = FileUtil.computeHash(uploadFileName);
         if (orig.canRead() || (oldMedia != null && oldMedia.size() > 0)) {
            String revSuffix = "1";
            if (oldMedia != null) {
               int maxRev = 0;
               for (ManagedMedia oldImage:oldMedia) {
                  if (java.util.Arrays.equals(oldImage.fileHash, origHash)) {
                     newFiles.add(oldImage.uniqueFileName);
                     errors.add("Media for: " + fileName + " already exists. Last modified: " + oldImage.lastModified);
                     found = true;
                     revSuffix = oldImage.revision;
                     break;
                  }
                  if (oldImage.revision != null) {
                     try {
                        int oldRev = Integer.parseInt(oldImage.revision);
                        if (oldRev > maxRev)
                           maxRev = oldRev;
                     }
                     catch (NumberFormatException exc) {
                     }
                  }
               }
               if (found)
                  continue;

               maxRev++;
               revSuffix = String.valueOf(maxRev);

            }
            revision = revSuffix;
            origFileName = FileUtil.concat(mediaStore.origDir, useManager.getOrigFileName(fileName, revision, info.fileType));
         }

         if (!found) {
            if (!FileUtil.copyFile(uploadFileName, origFileName, false)) {
               errors.add("Error copying media to storage");
               break;
            }

            ManagedImage newIm = new ManagedImage();
            newIm.width = info.width;
            newIm.height = info.height;
            newIm.fileName = FileUtil.removeExtension(FileUtil.getFileName(fileName));
            newIm.fileType = info.fileType;
            newIm.fileHash = origHash;
            newIm.revision = revision;
            newIm.manager = useManager;
            newIm.dbInsert(false);
            newIm.mediaChanged();

            if (newIm.mediaError != null)
               errors.add(newIm.mediaError);
            newFiles.add(newIm.uniqueFileName);
         }
      }
      return new UploadResult(newFiles.size() > 0 ? newFiles : null, errors.size() == 0 ? null : errorsToString(errors));
   }

   String errorsToString(List<String> errors) {
      if (errors.size() == 1)
         return errors.get(0);
      else
         return "Multiple errors: " + errors;
   }
}
