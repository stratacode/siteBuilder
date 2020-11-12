class BlogPostView extends BlogElementView {
   @Sync(initDefault=true)
   BlogPost post;

   post =: validateManagedElement();

   // There is a catalogElement property here but it's not bindable so need to put this in each subclass
   elementMedia := post.altMedia;
   elementMainMedia := post.mainMedia;
   elementMediaChangedCt := post.mediaChangedCt;

   @Sync
   String postViewError;

   ManagedElement getManagedElement() {
      return post;
   }
}
