package sc.user;

import sc.user.UserProfile;
import sc.user.Userbase;
import sc.user.Address;
import sc.user.LoginStatus;
import sc.user.ContactType;
import sc.user.SessionEvent;

@Sync(syncMode=SyncMode.Automatic)
public user.model extends util, db.model, user.addressDB.model, user.dataSource {
   compiledOnly = true;
}
