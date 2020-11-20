@Sync(onDemand=true)
class ContentView implements IView {
   @Bindable
   ViewDef viewDef;

   @sc.obj.HTMLSettings(returnsHTML=true)
   @Sync(initDefault=true)
   String contentHtml;
}
