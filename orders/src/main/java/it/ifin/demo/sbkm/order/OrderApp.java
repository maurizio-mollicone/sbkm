package it.ifin.demo.sbkm.order;

import java.time.Duration;
import java.util.concurrent.Executor;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import it.ifin.demo.sbkm.base.Const;
import it.ifin.demo.sbkm.base.domain.Order;
import it.ifin.demo.sbkm.order.service.OrderManageService;

@SpringBootApplication
@EnableKafkaStreams
@EnableAsync
public class OrderApp {

    private static final Logger LOG = LoggerFactory.getLogger(OrderApp.class);

    @Autowired
    OrderManageService orderManageService;
    
    public static void main(String[] args) {
        SpringApplication.run(OrderApp.class, args);
    }

    @Bean
    public NewTopic orders() {
        return TopicBuilder.name(Const.Topic.ORDERS)
                .partitions(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name(Const.Topic.PAYMENT_ORDERS)
                .partitions(3)
                .compact()
                .build();
    }

    @Bean
    public NewTopic stockTopic() {
        return TopicBuilder.name(Const.Topic.STOCK_ORDERS)
                .partitions(3)
                .compact()
                .build();
    }



    @Bean
    public KStream<Long, Order> stream(StreamsBuilder builder) {
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);
        KStream<Long, Order> stream = builder
                .stream(Const.Topic.PAYMENT_ORDERS, Consumed.with(Serdes.Long(), orderSerde));

        stream.join(
                builder.stream(Const.Topic.STOCK_ORDERS),
                orderManageService::confirm,
                JoinWindows.of(Duration.ofSeconds(10)),
                StreamJoined.with(Serdes.Long(), orderSerde, orderSerde))
                .peek((k, o) -> LOG.info("Output: {}", o))
                .to(Const.Topic.ORDERS);

        return stream;
    }

    @Bean
    public KTable<Long, Order> table(StreamsBuilder builder) {
        KeyValueBytesStoreSupplier store = Stores.persistentKeyValueStore(Const.Topic.ORDERS);
        JsonSerde<Order> orderSerde = new JsonSerde<>(Order.class);
        KStream<Long, Order> stream = builder
                .stream(Const.Topic.ORDERS, Consumed.with(Serdes.Long(), orderSerde));
        return stream.toTable(Materialized.<Long, Order>as(store)
                .withKeySerde(Serdes.Long())
                .withValueSerde(orderSerde));
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("kafkaSender-");
        executor.initialize();
        return executor;
    }
}