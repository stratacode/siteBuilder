@Sync(onDemand=true)
@CompilerSettings(compiledOnly=true)
abstract class ViewDef implements IPropValidator {
   transient SiteContext site;

   // Need to handle heterogenous lists in JSON parser
   //String viewClassName;

   transient @Bindable
   @DBPropertySettings(persist=false)
   Map<String,String> propErrors = null;

   void dispose() {
      DynUtil.dispose(this, false);
   }
}
