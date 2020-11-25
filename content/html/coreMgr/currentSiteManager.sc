// So that the current site is shared as we go from page to page
// create a new site etc. An alternative would be to pass the siteName
// around in the URL and rewrite all of the links to include the current
// site name. That would avoid the 'browser wide' setting of the current
// site if you have multiple tabs going to different stores.
scope<window>
object currentSiteManager extends SiteManager {
}
