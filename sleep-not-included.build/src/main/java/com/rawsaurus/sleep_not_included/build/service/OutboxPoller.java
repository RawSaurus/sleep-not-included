package com.rawsaurus.sleep_not_included.build.service;

import com.rawsaurus.sleep_not_included.build.model.OutboxEvent;
import com.rawsaurus.sleep_not_included.build.repo.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private final OutboxEventRepository outboxRepo;
    private final RabbitTemplate rabbitTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void pollAndPublish(){
        List<OutboxEvent> pending = outboxRepo.findByPublishedFalseOrderByCreatedAtAsc();

        for(OutboxEvent e : pending){
            rabbitTemplate.convertAndSend(
                    e.getExchange(),
                    "",
                    e.getPayload()
            );

            e.setPublished(true);
            outboxRepo.save(e);
        }
    }
}
