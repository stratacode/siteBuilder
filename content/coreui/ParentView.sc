@Sync(onDemand=true)
scope<appSession>
abstract class ParentView implements IView {
   @Sync(initDefault=true)
   List<IView> childViews;

}