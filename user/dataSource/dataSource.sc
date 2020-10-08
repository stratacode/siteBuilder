import java.util.Date;

import sc.obj.SyncMode;
import sc.obj.Sync;

public user.dataSource extends db.model {
   compiledOnly = true;

   object userDataSource extends DBDataSource {
      jndiName = "jdbc/scecom";
      dbName = "scecom";
      userName = "sctest";
      password = "sctest";
      serverName = "localhost";
      port = 5432;
   }
}
