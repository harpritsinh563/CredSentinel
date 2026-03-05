package com.credsentinel.loandecision.component;

import com.credsentinel.loandecision.model.LoanDecisionMadeEvent;
import com.credsentinel.loandecision.model.LoanRequestCreatedEvent;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
@SpringBootTest
@EmbeddedKafka(
        controlledShutdown = true,
        partitions = 1,
        topics = {"CredSentinel.LoanRequestCreated", "CredSentinel.LoanDecisionMade"},
        bootstrapServersProperty = "spring.kafka.bootstrap-servers",
        brokerProperties = {
                "group.initial.rebalance.delay.ms=0",
                "offsets.topic.num.partitions=1",
                "offsets.topic.replication.factor=1",
                "transaction.state.log.replication.factor=1",
                "transaction.state.log.min.isr=1"
        }
)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class LoanDecisionComponentTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    private Consumer<String, LoanDecisionMadeEvent> decisionConsumer;

    @BeforeEach
    void setup() {
        // 1. Wait for Spring @KafkaListener containers to be ready
        for (MessageListenerContainer container : registry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());
        }

        // 2. Setup a manual consumer with a UNIQUE group ID to avoid the rebalance loop
        String uniqueGroupId = "test-group-" + UUID.randomUUID();
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(uniqueGroupId, "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        JsonDeserializer<LoanDecisionMadeEvent> resultDeserializer = new JsonDeserializer<>(LoanDecisionMadeEvent.class);
        resultDeserializer.addTrustedPackages("*");

        decisionConsumer = new DefaultKafkaConsumerFactory<>(
                consumerProps,
                new StringDeserializer(),
                resultDeserializer
        ).createConsumer();

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(decisionConsumer, "CredSentinel.LoanDecisionMade");
    }

    @AfterEach
    void tearDown() {
        // 3. CRITICAL: Close the consumer to release Windows file locks and stop rebalancing
        if (decisionConsumer != null) {
            decisionConsumer.close();
        }
    }

    @Test
    void shouldApproveLoanRequest_WhenUserIsVerifiedAndAmountIsLow() {
        // Arrange
        String loanId = "LOAN-" + UUID.randomUUID();
        LoanRequestCreatedEvent requestEvent = new LoanRequestCreatedEvent(
                loanId, "user-123", new BigDecimal("5000"), 60, 750, Instant.now()
        );

        // Act
        kafkaTemplate.send("CredSentinel.LoanRequestCreated", loanId, requestEvent);

        // Assert
        ConsumerRecord<String, LoanDecisionMadeEvent> record =
                KafkaTestUtils.getSingleRecord(decisionConsumer, "CredSentinel.LoanDecisionMade");

        assertThat(record).isNotNull();
        assertThat(record.value().getLoanRequestId()).isEqualTo(loanId);
        assertThat(record.value().getDecision()).isEqualTo("APPROVED");
    }

    @Test
    void shouldRejectLoanRequest_WhenTenureIsTooLong() {
        // Arrange
        String loanId = "LOAN-" + UUID.randomUUID();
        LoanRequestCreatedEvent requestEvent = new LoanRequestCreatedEvent(
                loanId, "user-456", new BigDecimal("5000"), 1000, 750, Instant.now()
        );

        // Act
        kafkaTemplate.send("CredSentinel.LoanRequestCreated", loanId, requestEvent);

        // Assert: Poll for records and find the specific one for this loanId
        ConsumerRecord<String, LoanDecisionMadeEvent> foundRecord = null;
        long stopTime = System.currentTimeMillis() + 10000; // 10s timeout

        while (System.currentTimeMillis() < stopTime && foundRecord == null) {
            ConsumerRecords<String, LoanDecisionMadeEvent> records =
                    decisionConsumer.poll(Duration.ofMillis(500));

            for (ConsumerRecord<String, LoanDecisionMadeEvent> record : records) {
                if (record.key().equals(loanId)) {
                    foundRecord = record;
                    break;
                }
            }
        }

        assertThat(foundRecord).isNotNull();
        assertThat(foundRecord.value().getDecision()).isEqualTo("REJECTED");
    }
}