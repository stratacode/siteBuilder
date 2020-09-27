import java.util.TreeSet;
import java.util.Arrays;

import sc.lang.html.HTMLElement;

BaseProductManager {
   longDescHtml =: updateLongDesc(longDescHtml);

   void storeChanged() {
      resetForm();
   }

   void updateLongDesc(String htmlText) {
      if (element == null)
         return;
      String error = HTMLElement.validateClientHTML(htmlText, HTMLElement.formattingTags, HTMLElement.formattingAtts);
      if (error == null)
         element.longDesc = htmlText;
      else
         System.err.println("Invalid html text submission: " + htmlText + ": " + error);
   }


   String getMediaFilterPattern() {
      return null;
   }

   void updateMatchingMedia(String pattern) {
      if (pattern == null || pattern.length() < 2)
         return;
      TreeSet<String> found = new TreeSet<String>();
      ArrayList<ManagedMedia> res = new ArrayList<ManagedMedia>();
      List<ManagedMedia> allMatches = (List<ManagedMedia>) ManagedMedia.getDBTypeDescriptor().searchQuery(pattern, null, null, null, searchOrderBy, 0, 20);
      for (ManagedMedia match:allMatches) {
         if (!found.contains(match.uniqueFileName)) {
            res.add(match);
            found.add(match.uniqueFileName);
            if (res.size() == 10)
               break;
         }
      }
      matchingMedia = res;
   }

   void updateElementMedia(String uniqueFileName) {
      String fileName = MediaManager.removeRevisionFromFile(uniqueFileName);
      List<ManagedMedia> mediaList = ManagedMedia.findByFileName(fileName, store.mediaManager);
      mediaStatusMessage = null;
      mediaErrorMessage = null;
      findMediaText = uniqueFileName;

      String newFilter = getMediaFilterPattern();

      if (mediaList != null) {
         for (ManagedMedia media:mediaList) {
            if (media.uniqueFileName.equals(uniqueFileName)) {
               List<ManagedMedia> prodMedia = element.altMedia;
               boolean setList = false;
               if (prodMedia == null) {
                  prodMedia = new ArrayList<ManagedMedia>();
                  setList = true;
               }
               else if (prodMedia.contains(media)) {
                  if (newFilter != null) {
                     String oldFilter = media.filterPattern;
                     if (oldFilter != null && !oldFilter.equals(newFilter)) {
                        mediaStatusMessage = "Changed filter pattern from: " + oldFilter + " to: " + newFilter;
                        media.filterPattern = newFilter;
                        findMediaText = "";
                        return;
                     }
                  }
                  mediaErrorMessage = "Media file: " + media.uniqueFileName + " already in product";
                  findMediaText = "";
                  return;
               }

               String extraStatus = "";
               String oldFilter = media.filterPattern;
               if (newFilter != null) {
                  if (oldFilter != null && !oldFilter.equals(newFilter)) {
                     extraStatus = " and replaced old options: " + oldFilter + " to: " + newFilter;
                  }
               }
               else if (media.filterPattern != null) {
                  extraStatus = " and cleared old options: " + oldFilter;
               }
               media.filterPattern = newFilter;

               mediaStatusMessage = "Added file: " + media.uniqueFileName + " to media" + extraStatus;
               prodMedia.add(media);
               if (setList) {
                  element.altMedia = prodMedia;
                  element.mainMedia = media;
               }
               findMediaText = "";
               return;
            }
         }
      }

      mediaErrorMessage = "No media found with fileName: " + uniqueFileName;
   }

   void removeElementMedia(long mediaId) {
      clearMediaErrors();
      if (element == null || element.altMedia == null)
         mediaErrorMessage = "No product or media for remove";
      else {
         for (int i = 0; i < element.altMedia.size(); i++) {
            if (element.altMedia.get(i).id == mediaId) {
               element.altMedia.remove(i);
               return;
            }
         }
         mediaErrorMessage = "Remove media: media not found";
      }
   }

   void updateMediaVisible(ManagedMedia media, boolean newVis) {
      media.visible = newVis;
      if (element.altMedia.contains(media))
         Bind.sendChangedEvent(element.altMedia, null);
   }

   void addMediaResult(Object res) {
      if (res instanceof Object[])
         res = Arrays.asList((Object[]) res);
      List resList = (List) res;
      clearMediaErrors();
      if (resList.size() == 1) {
         String nameWithRev = (String) resList.get(0);
         // Here we want to find all versions of the file uploaded just for context
         List<ManagedMedia> mediaRes = MediaManagerView.searchForText(nameWithRev, store.mediaManager);
         int selIx = -1;
         ManagedMedia newMedia = null;
         for (int i = 0; i < mediaRes.size(); i++) {
            if (mediaRes.get(i).uniqueFileName.equals(nameWithRev)) {
               newMedia = mediaRes.get(i);
               selIx = i;
               break;
            }
         }
         if (newMedia != null) {
            String newFilter = getMediaFilterPattern();
            if (newFilter != null) {
               String oldFilter = newMedia.filterPattern;
               if (oldFilter != null && !oldFilter.equals(newFilter)) {
                  System.out.println("Changing filter pattern from: " + oldFilter + " to: " + newFilter);
               }
               newMedia.filterPattern = newFilter;
            }

            List<ManagedMedia> altMedia = element.altMedia;
            boolean setList = false;
            if (altMedia == null) {
               altMedia = new ArrayList<ManagedMedia>();
               setList = true;
            }
            if (altMedia.contains(newMedia)) {
            // The result may have already been uploaded and added to the product
               mediaErrorMessage = "Media already exists in " + elementType + ": " + nameWithRev;
               return;
            }
            else
               altMedia.add(newMedia);
            if (setList)
               element.altMedia = altMedia;
            if (element.mainMedia == null)
               element.mainMedia = newMedia;

            mediaStatusMessage = "Added media file: " + newMedia.uniqueFileName + " " + newMedia.fileType + " " + newMedia.width + "x" + newMedia.height + (newFilter == null ? "" : " for options: " + newFilter);
         }
         else
            mediaErrorMessage = "No media: " + nameWithRev;
      }
      else {
         System.err.println("*** Multiple files returned for add product media - expecting only one");
      }
   }

   void addMediaError(String err) {
      mediaStatusMessage = null;
      mediaErrorMessage = err;
   }

   void startAddCategory() {
      if (addCategoryInProgress)
         return;
      initTemporaryCategory();
      addCategoryInProgress = true;
      showCategoryView = true;
      categoryStatusMessage = categoryErrorMessage = null;
      categoryEditable = false;
      autoUpdateCategoryPath = true;
   }

   void initTemporaryCategory() {
      category = (Category) Category.getDBTypeDescriptor().createInstance();
      category.store = store;
      category.name = "";
      category.pathName = "";
      category.shortDesc = "";
      category.longDesc = "";
   }

   abstract void newCategoryCompleted(Category category);

   void completeAddCategory() {
      if (!addCategoryInProgress)
         return;

      try {
         clearFormErrors();
         category.validateProperties();
         if (category.propErrors == null) {
            category.store = store;
            category.dbInsert(false);

            addCategoryInProgress = false;
            showCategoryView = false;
            categoryStatusMessage = "Category added";
            categoryErrorMessage = null;
            categoryEditable = true;

            newCategoryCompleted(category);
         }
         else {
            categoryErrorMessage = category.formatErrors();
            categoryStatusMessage = null;
         }
      }
      catch (IllegalArgumentException exc) {
         categoryStatusMessage = null;
         categoryErrorMessage = "System error: " + exc;
      }
   }

   void cancelAddCategory() {
      addCategoryInProgress = false;
      category = null;
      categoryErrorMessage = null;
      categoryStatusMessage = "Add category cancelled";
      showCategoryView = false;
   }

   void doneEditingCategory() {
      addCategoryInProgress = false;
      categoryErrorMessage = null;
      categoryStatusMessage = null;
      showCategoryView = false;
   }

}
