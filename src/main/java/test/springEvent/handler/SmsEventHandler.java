package test.springEvent.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import test.springEvent.event.RegisteredEvent;
import test.springEvent.service.AlarmService;
import test.springEvent.service.AlimTalkService;

@Component
@RequiredArgsConstructor
public class SmsEventHandler {

    private final AlarmService alarmService;
    private final AlimTalkService alimTalkService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendFCM(RegisteredEvent event) throws InterruptedException {
        alarmService.send(event.getName());
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendAlimTalk(RegisteredEvent event) throws Exception {
        alimTalkService.send(event.getName());
    }
}
