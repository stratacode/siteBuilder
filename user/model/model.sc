package sc.user;

import sc.user.UserProfile;
import sc.user.Userbase;
import sc.user.Address;
import sc.user.LoginStatus;

@Sync(syncMode=SyncMode.Automatic)
public user.model extends util, db.model, user.postalCodeDB.model, user.dataSource {
   compiledOnly = true;
}
