import java.util.HashMap;

@Component
@CompilerSettings(constructorProperties="pathName")
class StoreView extends SiteView {
   @Sync(initDefault=true)
   Storefront store;

   /*
   public StoreView(String sitePathName) {
      super(sitePathName);
   }
   */

   OrderView orderView; 
}
