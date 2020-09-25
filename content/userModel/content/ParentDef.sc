@Sync
abstract class ParentDef extends ViewDef {
   @Sync(initDefault=true)
   List<ViewDef> childViewDefs;

}
