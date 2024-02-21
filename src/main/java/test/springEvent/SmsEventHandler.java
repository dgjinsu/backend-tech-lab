package test.springEvent;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import test.springEvent.service.AlarmService;
import test.springEvent.service.AlimTalkService;

@Component
@RequiredArgsConstructor
public class SmsEventHandler {

    private final AlarmService alarmService;
    private final AlimTalkService alimTalkService;

    @Async // 추가
    @EventListener
    public void sendFCM(RegisteredEvent event) throws InterruptedException {
        System.out.println("[SmsEventHandler - sendFCM]");
        alarmService.send(event.getName());
    }

    @Async // 추가
    @EventListener
    public void sendAlimTalk(RegisteredEvent event) throws Exception {
        System.out.println("[SmsEventHandler - sendAlimTalk]");
        alimTalkService.send(event.getName());
    }
}
