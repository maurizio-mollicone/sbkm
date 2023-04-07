package it.ifin.demo.sbkm.payments;

import java.security.SecureRandom;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;

import it.ifin.demo.sbkm.base.Const;
import it.ifin.demo.sbkm.base.domain.Order;
import it.ifin.demo.sbkm.payments.domain.Customer;
import it.ifin.demo.sbkm.payments.repository.CustomerRepository;
import it.ifin.demo.sbkm.payments.service.OrderService;
import jakarta.annotation.PostConstruct;
import net.datafaker.Faker;

@SpringBootApplication
@EnableKafka
public class PaymentApp {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentApp.class);

    public static void main(String[] args) {
        SpringApplication.run(PaymentApp.class, args);
    }

    @Autowired
    OrderService orderService;

    @KafkaListener(id = "orders", topics = "orders", groupId = "payment")
    public void onEvent(Order o) {
        LOG.info("Received: {}" , o);
        if (o.getStatus().equals(Const.Status.NEW))
            orderService.reserve(o);
        else
            orderService.confirm(o);
    }

    @Autowired
    private CustomerRepository repository;
    
    @PostConstruct
    public void generateData() {
        
        Random r = new Random();
        Faker faker = new Faker();
        for (int i = 0; i < 100; i++) {
            int count = r.nextInt(100, 1000);
            Customer c = new Customer(null, faker.name().fullName(), count, 0);
            repository.save(c);
        }
    }
}

