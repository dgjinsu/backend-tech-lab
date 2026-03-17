package com.example.rabbitmqparallel.consumer;

import com.example.rabbitmqparallel.dto.MessageProcessingRecord;
import com.example.rabbitmqparallel.dto.TaskMessage;
import com.example.rabbitmqparallel.tracking.ProcessingTracker;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MessageConsumer {

    private final HeavyWorkSimulator heavyWorkSimulator;
    private final ProcessingTracker tracker;

    @RabbitListener(
            queues = "${queue.name}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void consume(TaskMessage message, Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        String threadName = Thread.currentThread().getName();
        long receivedAt = System.currentTimeMillis();

        tracker.recordReceived(message.getMessageId(), message.getSequenceNumber(),
                message.getPublishedAt(), receivedAt, threadName);
        tracker.incrementConcurrent();

        try {
            long startAt = System.currentTimeMillis();
            List<MessageProcessingRecord.SubCallRecord> subCalls =
                    heavyWorkSimulator.simulateHeavyWork(message.getMessageId());
            long endAt = System.currentTimeMillis();

            tracker.recordCompleted(message.getMessageId(), startAt, endAt,
                    subCalls, true, null);

            channel.basicAck(deliveryTag, false);

            log.info("[ACK] seq={} processed in {}ms by {} (concurrent={})",
                    message.getSequenceNumber(), endAt - startAt,
                    threadName, tracker.getCurrentConcurrent());

        } catch (Exception e) {
            log.error("[NACK] seq={} failed: {}", message.getSequenceNumber(), e.getMessage());
            tracker.recordCompleted(message.getMessageId(),
                    System.currentTimeMillis(), System.currentTimeMillis(),
                    List.of(), false, e.getMessage());

            try {
                channel.basicNack(deliveryTag, false, false);
            } catch (IOException ioEx) {
                log.error("Failed to NACK seq={}: {}", message.getSequenceNumber(), ioEx.getMessage());
            }
        } finally {
            tracker.decrementConcurrent();
        }
    }
}
