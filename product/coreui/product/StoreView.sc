import java.util.HashMap;

@Component
object StoreView {
   @Sync(initDefault=true)
   Storefront store;

   String pathName;

}
