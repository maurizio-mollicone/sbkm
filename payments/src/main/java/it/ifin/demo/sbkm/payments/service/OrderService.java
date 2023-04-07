package it.ifin.demo.sbkm.payments.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import it.ifin.demo.sbkm.base.Const;
import it.ifin.demo.sbkm.base.domain.Order;
import it.ifin.demo.sbkm.payments.domain.Customer;
import it.ifin.demo.sbkm.payments.repository.CustomerRepository;

@Service
public class OrderService {
    
    
    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private CustomerRepository repository;
    private KafkaTemplate<Long, Order> template;

    public OrderService(CustomerRepository repository, KafkaTemplate<Long, Order> template) {
        this.repository = repository;
        this.template = template;
    }

    public void reserve(Order order) {
        Customer customer = repository.findById(order.getCustomerId()).orElseThrow();
        LOG.info("Found: {}", customer);
        if (order.getPrice() < customer.getAmountAvailable()) {
            order.setStatus(Const.Status.ACCEPT);
            customer.setAmountReserved(customer.getAmountReserved() + order.getPrice());
            customer.setAmountAvailable(customer.getAmountAvailable() - order.getPrice());
        } else {
            order.setStatus(Const.Status.REJECT);
        }
        order.setSource(Const.Topic.PAYMENT);
        repository.save(customer);
        template.send(Const.Topic.PAYMENT_ORDERS, order.getId(), order);
        LOG.info("Sent: {}", order);
    }

    public void confirm(Order order) {
        Customer customer = repository.findById(order.getCustomerId()).orElseThrow();
        LOG.info("Found: {}", customer);
        if (order.getStatus().equals(Const.Status.CONFIRMED)) {
            customer.setAmountReserved(customer.getAmountReserved() - order.getPrice());
            repository.save(customer);
        } else if (order.getStatus().equals(Const.Status.ROLLBACK) && !order.getSource().equals(Const.Topic.PAYMENT)) {
            customer.setAmountReserved(customer.getAmountReserved() - order.getPrice());
            customer.setAmountAvailable(customer.getAmountAvailable() + order.getPrice());
            repository.save(customer);
        }

    }
}
