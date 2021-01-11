
import java.util.HashMap;
import java.util.TreeMap;

scope<global>
@Component
object userSessionCache {
   Map<Long,Map<Long,Map<Long,UserSession>>> cacheByUserId = new HashMap<Long, Map<Long, Map<Long,UserSession>>>();
   Map<Long,Map<String,Map<Long,UserSession>>> cacheByUserMarker = new HashMap<Long,Map<String, Map<Long,UserSession>>>();

   boolean invalidateScheduled = false;
   int invalidateTimerDelay = 5000;

   synchronized void scheduleInvalidate() {
      invalidateScheduled = true;
      PTypeUtil.addScheduledJob(new Runnable() {
         public void run() {
            invalidateUserSessions(false);
         }
      }, invalidateTimerDelay, false);
   }

   void invalidateUserSessions(boolean doAll) {
      boolean needsInvalidate = false;

      List<UserSession> needsSave = null;
      List<UserSession> needsExpire = null;
      List<UserSession> needsMarkerExpire = null;

      synchronized (this) {
         for (Map<Long,Map<Long,UserSession>> ubMap:cacheByUserId.values()) {
            for (Map<Long,UserSession> userMap:ubMap.values()) {
               for (UserSession us:userMap.values()) {
                  if ((us.getDBObject().isTransient() || us.changedSession) && (doAll || us.needsSave())) {
                     if (needsSave == null)
                        needsSave = new ArrayList<UserSession>();
                     needsSave.add(us);
                  }
                  if (doAll || us.isExpired()) {
                     if (needsExpire == null)
                        needsExpire = new ArrayList<UserSession>();
                     needsExpire.add(us);
                  }
                  else
                     needsInvalidate = true;
               }
            }
         }
         for (Map<String,Map<Long,UserSession>> ubMap:cacheByUserMarker.values()) {
            for (Map<Long,UserSession> userMap:ubMap.values()) {
               for (UserSession us:userMap.values()) {
                  if ((us.changedSession || us.getDBObject().isTransient()) && (doAll || us.needsSave())) {
                     if (needsSave == null)
                        needsSave = new ArrayList<UserSession>();
                     needsSave.add(us);
                  }
                  if (doAll || us.isExpired()) {
                     if (needsMarkerExpire == null)
                        needsMarkerExpire = new ArrayList<UserSession>();
                     needsMarkerExpire.add(us);
                  }
                  else
                     needsInvalidate = true;
               }
            }
         }

         if (needsExpire != null) {
            //System.out.println("*** Invalidating: " + needsExpire.size() + " userSessions");
            for (UserSession us:needsExpire) {
               Userbase userbase = us.site.userbase;
               if (userbase == null)
                  userbase = Userbase.findByAppName(Userbase.defaultAppName);
               Map<Long,Map<Long,UserSession>> ubMap = cacheByUserId.get(userbase.id);
               UserSession removed;
               Map<Long,UserSession> siteMap = ubMap.get(us.user.id);
               if ((removed = siteMap.remove(us.site.id)) == null)
                  System.out.println("*** Failed to find user to remove");
               else if (siteMap.size() == 0) {
                  ubMap.remove(us.user.id);
               }
               if (removed != us)
                  System.err.println("*** Problem with user session cache!");
               DynUtil.dispose(us);
            }
         }
         if (needsMarkerExpire != null) {
            System.out.println("*** Invalidating: " + needsExpire.size() + " marker based userSessions");
            for (UserSession us:needsMarkerExpire) {
               Userbase userbase = us.site.userbase;
               if (userbase == null)
                  userbase = Userbase.findByAppName(Userbase.defaultAppName);
               Map<String,Map<Long,UserSession>> ubMap = cacheByUserMarker.get(userbase.id);
               UserSession removed;
               Map<Long,UserSession> siteMap = ubMap.get(us.userMarker);
               if ((removed = siteMap.remove(us.site.id)) == null)
                  System.out.println("*** Failed to find user to remove for marker");
               else if (siteMap.size() == 0)
                  ubMap.remove(us.userMarker);
               if (removed != us)
                  System.err.println("*** Problem with user session marker cache!");
               DynUtil.dispose(us);
            }
         }
      }
      if (needsSave != null) {
         DBTransaction tx = null;
         boolean success = false;
         try {
            tx = DBTransaction.getOrCreate();
            //System.out.println("*** Saving: " + needsSave.size() + " userSessions");
            for (UserSession us:needsSave) {
               us.changedSession = false;
               if (us.getDBObject().isTransient())
                  us.dbInsert(true);
               else {
                  us.sessionEvents = us.sessionEvents; // Force this JSON property to be resaved
                  us.dbUpdate();
               }
            }
            success = true;
         }
         catch (RuntimeException exc) {
            DBUtil.error("Error trying to to insert userSessions: " + exc);
         }
         finally {
            if (success)
               tx.commit();
            else {
               tx.rollback();
            }
            tx.close();
         }
      }
      else {
         DBTransaction tx = null;
         boolean success = false;
         tx = DBTransaction.getCurrent();
         if (tx != null) {
            if (tx.getNumOps() > 0)
               System.err.println("*** Transaction with operations after cache check");
            tx.close();
         }
      }
      if (needsInvalidate) {
         scheduleInvalidate();
      }
      else
         invalidateScheduled = false;
   }

   synchronized UserSession getOrCreateUserSession(Userbase userbase, UserProfile user, String userMarker, SiteContext site) {
      if (user != null) {
         if (user.id == 0) {
            System.err.println("User id not set in getUserSession");
            return null;
         }
         Map<Long,Map<Long,UserSession>> userIdCache = cacheByUserId.get(userbase.id);
         if (userIdCache == null) {
            userIdCache = new HashMap<Long,Map<Long,UserSession>>();
            cacheByUserId.put(userbase.id, userIdCache);
         }
         UserSession session = getOrCreateUserSessionForSite(userIdCache, user, site);
         return session;
      }
      else if (userMarker != null) {
         Map<String,Map<Long,UserSession>> userMarkerCache = cacheByUserMarker.get(userbase.id);
         if (userMarkerCache == null) {
            userMarkerCache = new HashMap<String,Map<Long,UserSession>>();
            cacheByUserMarker.put(userbase.id, userMarkerCache);
         }
         UserSession session = getOrCreateMarkerSessionForSite(userMarkerCache, userMarker, site);
         return session;
      }
      return null;
   }

   UserSession getOrCreateUserSessionForSite(Map<Long,Map<Long,UserSession>> ubSessions, UserProfile user, SiteContext site) {
      Map<Long,UserSession> siteSessions = ubSessions.get(user.id);
      if (siteSessions == null) {
         siteSessions = new TreeMap<Long,UserSession>();
         ubSessions.put(user.id, siteSessions);
      }
      UserSession session = siteSessions.get(site.id);
      if (session != null && session.isExpired())
         session = null;
      if (session == null) {
         session = new UserSession();
         session.user = user;
         session.site = site;
         siteSessions.put(site.id, session);

         if (!invalidateScheduled)
            scheduleInvalidate();
      }
      else
         session.user = user; // In case the user was removed from the cache due to a logout and log back in
      return session;
   }

   UserSession getOrCreateMarkerSessionForSite(Map<String,Map<Long,UserSession>> ubSessions, String userMarker, SiteContext site) {
      Map<Long,UserSession> siteSessions = ubSessions.get(userMarker);
      if (siteSessions == null) {
         siteSessions = new TreeMap<Long,UserSession>();
         ubSessions.put(userMarker, siteSessions);
      }
      UserSession session = siteSessions.get(site.id);
      if (session != null && session.isExpired())
         session = null;
      if (session == null) {
         session = new UserSession();
         session.userMarker = userMarker;
         session.site = site;
         siteSessions.put(site.id, session);

         if (!invalidateScheduled)
            scheduleInvalidate();
      }
      return session;
   }

   void stop() {
      invalidateUserSessions(true);

   }
}