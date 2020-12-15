UserSession {
   @FindBy(paged=true, orderBy="-lastModified")
   SiteContext site;
}
