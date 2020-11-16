@sc.js.JSSettings(dependentTypes="sc.user.ProductEvent,sc.user.CategoryEvent,sc.user.AddToCartEvent,sc.product.PhysicalSku")
@sc.obj.SyncTypeFilter(typeNames={"sc.user.ProductEvent", "sc.user.CategoryEvent", "sc.user.AddToCartEvent", "sc.product.PhysicalSku"})
UserManager {
   boolean showOrders = true;

   HashMap<String, List<Order>> ordersByUserId;
}
