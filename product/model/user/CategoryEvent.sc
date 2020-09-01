class CategoryEvent extends SessionEvent {
   long categoryId;
   CategoryEvent(long pId) {
      categoryId = pId;
   }
}
