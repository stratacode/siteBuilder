import java.util.Map;

import sc.servlet.UploadResult;

@URL(pattern="/upload", mimeType="text/plain", testScripts={"none"})
class UploadTestPage extends UploadPage {
   uploadPath = "/tmp/imageUpload"; 

   UploadResult processUpload(Map<String,String> uploadedFiles, Map<String,String> formFields) {
      String result;
      if (uploadedFiles.size() == 1)
          result = "Uploaded: " + uploadedFiles.get("fileToUpload");
      else
         result = "Uploaded files: " + uploadedFiles;
      return new UploadResult(result, null);
   }
}
