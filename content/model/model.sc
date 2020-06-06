package sc.content;

import sc.content.ManagedResource;
import sc.content.ManagedImage;
import sc.content.ManagedMedia;

import java.util.Date;

import sc.obj.Sync;
import sc.obj.SyncMode;
import sc.sync.SyncManager;
import sc.sync.SyncManager.SyncContext;

//import static sc.type.PTypeUtil.testMode;
import sc.type.PTypeUtil;

@Sync(syncMode=SyncMode.Automatic)
public content.model extends user.model {
}
