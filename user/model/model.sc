package sc.user;

import sc.user.UserProfile;
import java.util.Date;

public user.model extends util, jdbc.pgsql {
   object userDataSource extends DBDataSource {
      provider = "postgresql";
      jndiName = "jdbc/sctest";
      dbName = "scecom";
      userName = "sctest";
      password = "sctest";
      serverName = "localhost";
      port = 5432;
   }
}
