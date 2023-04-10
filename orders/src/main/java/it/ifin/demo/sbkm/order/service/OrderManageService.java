package it.ifin.demo.sbkm.order.service;

import org.springframework.stereotype.Service;

import it.ifin.demo.sbkm.base.Const;
import it.ifin.demo.sbkm.base.domain.Order;

@Service
public class OrderManageService {

    public Order confirm(Order orderPayment, Order orderStock) {
        Order o = new Order(orderPayment.getId(),
                orderPayment.getCustomerId(),
                orderPayment.getProductId(),
                orderPayment.getProductCount(),
                orderPayment.getPrice());
        if (orderPayment.getStatus().equals(Const.Status.ACCEPT) &&
                orderStock.getStatus().equals(Const.Status.ACCEPT)) {
            o.setStatus(Const.Status.CONFIRMED);
        } else if (orderPayment.getStatus().equals(Const.Status.REJECT) &&
                orderStock.getStatus().equals(Const.Status.REJECT)) {
            o.setStatus(Const.Status.REJECTED);
        } else if (orderPayment.getStatus().equals(Const.Status.REJECT) ||
                orderStock.getStatus().equals(Const.Status.REJECT)) {
            String source = orderPayment.getStatus().equals(Const.Status.REJECT)
                    ? Const.Source.PAYMENT.toUpperCase()
                    : Const.Source.STOCK.toUpperCase();
            o.setStatus(Const.Status.ROLLBACK);
            o.setSource(source);
        }
        return o;
    }

}
