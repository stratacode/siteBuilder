import java.util.HashMap;

@Component
@CompilerSettings(constructorProperties="pathName")
class SiteView {
   @Sync(initDefault=true)
   SiteContext siteContext;

   /*
   public SiteView(String sitePathName) {
      this.pathName = sitePathName;
   }
   */

   String pathName;

   int pageVisitCount = 0;
}
