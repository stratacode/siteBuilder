import java.util.HashMap;

@Component
@CompilerSettings(constructorProperties="pathName")
@Sync(onDemand=true)
class StoreView extends SiteView {
   @Sync(initDefault=true)
   Storefront store;

   /*
   public StoreView(String sitePathName) {
      super(sitePathName);
   }
   */

   @Sync(initDefault=true)
   OrderView orderView; 
}
