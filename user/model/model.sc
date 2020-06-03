package sc.user;

import sc.user.UserProfile;
import sc.user.UserManager;
import sc.user.Address;
import sc.user.LoginStatus;

import java.util.Date;

import sc.obj.SyncMode;
import sc.obj.Sync;

@Sync(syncMode=SyncMode.Automatic)
public user.model extends util, db.model {
   object userDataSource extends DBDataSource {
      jndiName = "jdbc/scecom";
      dbName = "scecom";
      userName = "sctest";
      password = "sctest";
      serverName = "localhost";
      port = 5432;
   }
}
