package seven.ipay.messaging.rmq.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import seven.ipay.messaging.rmq.infrastructure.event.TransactionEngagedEvent;

import java.util.List;
import java.util.Map;

@Component
public class TransactionConsumer {
    private static Logger logger = LoggerFactory.getLogger(TransactionConsumer.class);

    @Value("4")
    private Integer maxRetries;

    public TransactionConsumer(){

    }

    @RabbitListener(queues = "ipay-withdraw")
    public void process(TransactionEngagedEvent transactionEngagedEvent, @Header(name = "x-death", required = false) List xDeath){

        if (this.isRetriesExhausted(xDeath)) {
            logger.info("Max retries exhausted. Acking message: {}", transactionEngagedEvent);
            return;
        }

        try {
            System.out.println("\n --------------------------- \n");
            System.out.println(transactionEngagedEvent);
            System.out.println("\n --------------------------- \n");
        } catch (Exception e) {
            logger.error("An error occurred while trying to process transaction, cause: {}. Routing message to dead letter exchange...",
                    e.getMessage()
            );

            throw new AmqpRejectAndDontRequeueException(e.getMessage(), e);
        }
    }

    private boolean isRetriesExhausted(@Nullable List xDeath) {
        if (xDeath != null) {
            var lastXDeath = (Map) xDeath.get(0);
            Long deathCount = (Long) lastXDeath.get("count");
            return deathCount > maxRetries;
        }

        return false;
    }
}
