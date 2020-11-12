package sc;

import sc.blog.BlogPost;
import sc.blog.BlogCategory;
import sc.blog.BlogLabel;
import sc.blog.BlogElement;

@Sync(syncMode=SyncMode.Automatic)
public blog.model extends util, user.model, content.userModel {
   compiledOnly = true;
}
