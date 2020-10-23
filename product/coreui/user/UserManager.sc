@sc.js.JSSettings(dependentTypes="sc.user.ProductEvent,sc.user.CategoryEvent,sc.product.PhysicalSku,sc.user.PageEvent")
@sc.obj.SyncTypeFilter(typeNames={"sc.user.ProductEvent", "sc.user.CategoryEvent", "sc.product.PhysicalSku"})
UserManager {
   boolean showOrders = true;

   HashMap<String, List<Order>> ordersByUserId;
}
