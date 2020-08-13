UserView {
   boolean savePaymentInfo;

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

   void registerAfterOrder(Order order) {
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
         }
         if (order.paymentInfo != null && savePaymentInfo) {
            List<PaymentInfo> infos = user.paymentInfos;
            boolean setPI = false;
            if (infos == null) {
               infos = new BArrayList<PaymentInfo>();
               setPI = true;
            }
            infos.add(order.paymentInfo);
            if (setPI)
               user.paymentInfos = infos;
            user.savePaymentInfo = true;
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
      }
   }
}