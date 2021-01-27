@Sync(onDemand=true)
@DBTypeSettings
class SurveyResponse {
   UserProfile user;     // when tracking anonymous users with cookies, points back to the user-id
   UserSession userSession;

   @DBPropertySettings(indexed=true)
   String questionCode;
   String answerCode;
   int answerIndex;

   Date lastModified;
}
