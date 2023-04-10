package it.ifin.demo.sbkm.stock.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import it.ifin.demo.sbkm.base.Const;
import it.ifin.demo.sbkm.base.domain.Order;
import it.ifin.demo.sbkm.stock.domain.Product;
import it.ifin.demo.sbkm.stock.repository.ProductRepository;

@Service
public class OrderService {
    
    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    private ProductRepository repository;
    private KafkaTemplate<Long, Order> template;

    public OrderService(ProductRepository repository, KafkaTemplate<Long, Order> template) {
        this.repository = repository;
        this.template = template;
    }

    public void reserve(Order order) {
        Product product = repository.findById(order.getProductId()).orElseThrow();
        LOG.info("Found: {}", product);
        if (order.getStatus().equals(Const.Status.NEW)) {
            if (order.getProductCount() < product.getAvailableItems()) {
                product.setReservedItems(product.getReservedItems() + order.getProductCount());
                product.setAvailableItems(product.getAvailableItems() - order.getProductCount());
                order.setStatus(Const.Status.ACCEPT);
                repository.save(product);
            } else {
                order.setStatus(Const.Status.REJECT);
            }
            template.send(Const.Topic.STOCK_ORDERS, order.getId(), order);
            LOG.info("Sent: {}", order);
        }
    }

    public void confirm(Order order) {
        Product product = repository.findById(order.getProductId()).orElseThrow();
        LOG.info("Found: {}", product);
        if (order.getStatus().equals(Const.Status.CONFIRMED)) {
            product.setReservedItems(product.getReservedItems() - order.getProductCount());
            repository.save(product);
        } else if (order.getStatus().equals(Const.Status.ROLLBACK) && !order.getSource().equals(Const.Source.STOCK)) {
            product.setReservedItems(product.getReservedItems() - order.getProductCount());
            product.setAvailableItems(product.getAvailableItems() + order.getProductCount());
            repository.save(product);
        }
    }

}
