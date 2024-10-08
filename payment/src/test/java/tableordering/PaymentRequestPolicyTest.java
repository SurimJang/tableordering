package tableordering;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.test.binder.MessageCollector;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.MimeTypeUtils;
import tableordering.config.kafka.KafkaProcessor;
import tableordering.domain.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentRequestPolicyTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PaymentRequestPolicyTest.class
    );

    @Autowired
    private KafkaProcessor processor;

    @Autowired
    private MessageCollector messageCollector;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    public PaymentRepository repository;

    @Test
    @SuppressWarnings("unchecked")
    public void test0() {
        //given:
        Payment entity = new Payment();

        entity.setId("N/A");
        entity.setPaymentAmount("N/A");
        entity.setUserId("N/A");
        entity.setOrderId("N/A");
        entity.setPaymentStatus("N/A");

        repository.save(entity);

        //when:

        MenuDecreasedEvent event = new MenuDecreasedEvent();

        event.setId("N/A");
        event.setMenuName("N/A");
        event.setMenuPrice("N/A");
        event.setQty("N/A");
        event.setCategoryId("N/A");

        PaymentApplication.applicationContext = applicationContext;

        ObjectMapper objectMapper = new ObjectMapper()
            .configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
            );
        try {
            String msg = objectMapper.writeValueAsString(event);

            processor
                .inboundTopic()
                .send(
                    MessageBuilder
                        .withPayload(msg)
                        .setHeader(
                            MessageHeaders.CONTENT_TYPE,
                            MimeTypeUtils.APPLICATION_JSON
                        )
                        .setHeader("type", event.getEventType())
                        .build()
                );

            //then:

            Message<String> received = (Message<String>) messageCollector
                .forChannel(processor.outboundTopic())
                .poll();

            assertNotNull("Resulted event must be published", received);

            PaymentCompleteEvent outputEvent = objectMapper.readValue(
                (String) received.getPayload(),
                PaymentCompleteEvent.class
            );

            LOGGER.info("Response received: {}", received.getPayload());

            assertEquals(String.valueOf(outputEvent.getId()), "N/A");
            assertEquals(String.valueOf(outputEvent.getPaymentAmount()), "N/A");
            assertEquals(String.valueOf(outputEvent.getUserId()), "N/A");
            assertEquals(String.valueOf(outputEvent.getOrderId()), "N/A");
            assertEquals(String.valueOf(outputEvent.getPaymentStatus()), "N/A");
        } catch (JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }
}
