class CategoryViewEditor extends sc.content.BaseViewEditor {
   pageTitle = "Category";
   CategoryViewDef categoryDef := (CategoryViewDef) viewDef;
   categoryDef =: categoryDef.validateCategoryPathName(categoryDef.categoryPathName);
   List<Category> matchingCats;
}
