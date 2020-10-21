@Sync
@CompilerSettings(compiledOnly=true)
object currentUserView extends UserView {
   userbase = currentUserbase.userbase;
}
