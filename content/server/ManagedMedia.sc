ManagedMedia {
   mediaChangedCt =: updateGeneratedFiles();

   void updateGeneratedFiles() {
      if (manager == null)
         System.out.println("*** No manager for media");
      else
         manager.refreshMedia(this);
   }
}
