UserView {
   void removePaymentInfo(PaymentInfo pi) {
      if (user != null && user.paymentInfos != null) {
         int ix = user.paymentInfos.indexOf(pi);
         if (ix != -1) {
            user.paymentInfos.remove(ix);
            if (user.paymentInfo == pi)
               user.paymentInfo = null;
         }
      }
   }

   void registerAfterOrder(Order order, Order nextOrder) {
      emailAddress = order.emailAddress;

      if (register()) {
         if (order.shippingAddress != null) {
            List<Address> addrs = user.addresses;
            boolean setAddrs = false;
            if (addrs == null) {
               addrs = new BArrayList<Address>();
               setAddrs = true;
            }
            addrs.add(order.shippingAddress);
            if (setAddrs)
               user.addresses = addrs;
            user.homeAddress = order.shippingAddress;

            if (nextOrder != null)
               nextOrder.shippingAddress = user.homeAddress;
         }
         if (order.paymentInfo != null && user.savePaymentInfo) {
            List<PaymentInfo> infos = user.paymentInfos;
            boolean setPI = false;
            if (infos == null) {
               infos = new BArrayList<PaymentInfo>();
               setPI = true;
            }
            if (!infos.contains(order.paymentInfo))
               infos.add(order.paymentInfo);
            if (setPI)
               user.paymentInfos = infos;
            user.paymentInfo = order.paymentInfo;

            if (nextOrder != null)
               nextOrder.paymentInfo = order.paymentInfo;
         }
      }
   }

   void changePaymentInfo(PaymentInfo pi) {
      if (user != null) {
         user.paymentInfo = pi;
      }
   }

   void savePaymentInfo(PaymentInfo pi) {
      if (user != null) {
         user.paymentInfo = pi;
         user.savePaymentInfo = true;
         if (user.paymentInfos == null) {
            user.paymentInfos = new BArrayList<PaymentInfo>();
         }
         else if (user.paymentInfos.contains(pi))
            return;
         user.paymentInfos.add(pi);
      }
   }

   void clearPaymentInfo() {
      if (user != null) {
         if (user.paymentInfo != null) {
            user.paymentInfo.clearCardInfo();
            user.paymentInfo = null;
         }
         if (user.paymentInfos != null) {
            for (PaymentInfo info:user.paymentInfos) {
               info.clearCardInfo();
            }
            user.paymentInfos = null;
         }
         user.savePaymentInfo = false;
      }
   }

   boolean login() {
      UserProfile anonUser = user;
      if (anonUser.registered) {
         orderView.orderError = "Already logged in";
         return true;
      }

      // If the user name is not set directly use the email address from the current order when calling from CartView
      if (userName == null || userName.length() == 0) {
         userName = orderView.order.emailAddress;
         emailAddress = emailAddress;
      }
      if (super.login()) {
         if (orderView != null) {
            UserProfile regUser = user;
            Order regUserOrder = OrderView.getPendingOrderForUser(regUser, orderView.store);
            if (regUserOrder == null) {
            // Make the registered user the owner of the anonymous cart
               orderView.order.user = regUser;
            }
            else {
            // Add the anonymous shopping cart to the existing registered user
               regUserOrder.appendOrder(orderView.order);
            }
            orderView.order = regUserOrder;
            orderView.refreshLineItems();
            orderView.refresh();
         }
      }
      else { // failed
         if (orderView != null)
            orderView.orderError = userViewError;
      }
      return true;
   }
}