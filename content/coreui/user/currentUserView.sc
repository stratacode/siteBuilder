@Sync
@CompilerSettings(compiledOnly=true)
scope<window>
object currentUserView extends UserView {
   userbase = currentUserbase.userbase;
}
