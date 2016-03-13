
// Making this an interface so we can add it anywhere.  We might want to add layers which
// implement this up at a higher level or add this to more components.
interface TrackableResource {
   Date startDate;
   Date endDate;
   Date lastModified;
   UserProfile lastUpdated;
}


