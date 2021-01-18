UserSession {
   @FindBy(paged=true, orderBy="-lastEventTime")
   SiteContext site;
}
