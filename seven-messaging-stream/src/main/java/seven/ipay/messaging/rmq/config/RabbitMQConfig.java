package seven.ipay.messaging.rmq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    private final RabbitProperties properties;

    public RabbitMQConfig (){
        this.properties = new RabbitProperties();
    }

    @Bean
    public ConnectionFactory defaultConnectionFactory(){
        com.rabbitmq.client.ConnectionFactory connectionFactory = new com.rabbitmq.client.ConnectionFactory();
        connectionFactory.setUsername(properties.getUsername());
        connectionFactory.setPassword(properties.getAddresses());
        connectionFactory.setHost(properties.getHost());
        connectionFactory.setPort(properties.getPort());

        System.out.println("\n --------------------------- \n");
        System.out.println(properties.getUsername());
        System.out.println("\n --------------------------- \n");

        CachingConnectionFactory cacheConnectionFactory = new CachingConnectionFactory(connectionFactory);
        cacheConnectionFactory.setHost(properties.getHost());
        cacheConnectionFactory.setPort(properties.getPort());
        cacheConnectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        return cacheConnectionFactory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory listenerContainerFactory(){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(defaultConnectionFactory());
        factory.setConcurrentConsumers(2);
        factory.setMaxConcurrentConsumers(10);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public RabbitAdmin ipayAdmin(){
        return new RabbitAdmin(defaultConnectionFactory());
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter(ObjectMapper objectMapper){
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
