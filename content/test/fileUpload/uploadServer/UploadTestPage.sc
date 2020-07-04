import java.util.Map;

@Exec(serverOnly=true)
@URL(pattern="/upload")
class UploadTestPage extends UploadPage {
   uploadPath = "/tmp/imageUpload"; 

   void processUpload(Map<String,String> uploadedFiles, Map<String,String> formFields) {
      System.out.println("Uploaded: " + uploadedFiles + " forms: " + formFields);
   }
}
