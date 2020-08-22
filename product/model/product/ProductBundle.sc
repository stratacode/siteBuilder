@DBTypeSettings(typeId=2)
class ProductBundle extends Product {
   @DBPropertySettings(columnType="jsonb")
   List<Product> parts;
}


