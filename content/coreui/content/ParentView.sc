@Sync(onDemand=true)
abstract class ParentView implements IView {
   @Sync(initDefault=true)
   List<IView> childViews;

}