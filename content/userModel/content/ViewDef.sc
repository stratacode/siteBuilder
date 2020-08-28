class ViewDef implements IPropValidator {
   transient SiteContext site;

   // Need to handle heterogenous lists in JSON parser
   //String viewClassName;

   @Bindable
   @DBPropertySettings(persist=false)
   Map<String,String> propErrors = null;
}