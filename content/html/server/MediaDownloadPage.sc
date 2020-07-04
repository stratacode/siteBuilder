
@URL(pattern="/images/gen/{urlPath}",resource=true,dynContent=false)
scope<global> class MediaDownloadPage extends DownloadPage {
   downloadPath = mediaStore.genDir;
   startUrl = "/images/gen";
   {
      mimeTypes.put("jpg", "image/jpeg");
      mimeTypes.put("png", "image/png");
   }
}