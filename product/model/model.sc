package sc;

import sc.obj.Constant;

import sc.product.Storefront;
import sc.product.Product;
import sc.product.Category;
import sc.product.Sku;
import sc.product.Currency;

import sc.product.PaymentInfo;
import sc.product.PaymentTransaction;
import sc.product.OptionScheme;
import sc.product.ProductOption;
import sc.product.OptionValue;

import sc.product.Order;

@Sync(syncMode=SyncMode.Automatic)
public product.model extends util, user.model, content.userModel {
   compiledOnly = true;
}
