@Sync(onDemand=true)
scope<appSession>
class ContentView implements IView {
   @Bindable
   ViewDef viewDef;

   @sc.obj.HTMLSettings(returnsHTML=true)
   @Sync(initDefault=true)
   String contentHtml;
}
