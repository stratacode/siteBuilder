package sc;

import sc.content.SiteContext;
import sc.content.PageDef;
import sc.content.ViewDef;
import sc.content.ParentDef;

import sc.user.UserSession;

// TODO: merge this layer into content.coreui?
public content.userModel extends content.model, user.model {
   compiledOnly = true;
}
