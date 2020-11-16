class BlogCategoryViewEditor extends sc.content.BaseViewEditor {
   pageTitle = "Blog category";
   BlogCategoryViewDef categoryDef := (BlogCategoryViewDef) viewDef;
   categoryDef =: categoryDef.validateCategoryPathName(categoryDef.categoryPathName);
   List<BlogCategory> matchingCats;
}
