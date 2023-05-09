package com.example.accountservice.service;

import com.example.accountservice.model.MessageDTO;
import com.example.accountservice.model.StatisticDTO;
import com.example.accountservice.repo.MessageRepo;
import com.example.accountservice.repo.StatisticRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PollingService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    MessageRepo messageRepo;

    @Autowired
    StatisticRepo statisticRepo;

    //@Scheduled(fixedDelay = 5000)
    @Scheduled(cron = "0 45 11 * * ?")
    public void producer(){
        List<MessageDTO> messageDTOS = messageRepo.findByStatus(false);

        for (MessageDTO messageDTO: messageDTOS) {
            kafkaTemplate.send("notification", messageDTO);
//            if (kafkaTemplate.send("notification", messageDTO).isDone()) {
//                logger.info("SUCCESS");
//                messageDTO.setStatus(true);
//                messageRepo.save(messageDTO);
//            } else {
//                logger.error("FAIL SEND MESSAGE DATA");
//            }
        }

        List<StatisticDTO> statisticDTOS = statisticRepo.findByStatus(false);
        for (StatisticDTO statisticDTO: statisticDTOS) {
            kafkaTemplate.send("statistic", statisticDTO);
//            if (kafkaTemplate.send("notification", statisticDTO).isDone()) {
//                logger.info("SUCCESS");
//                statisticDTO.setStatus(true);
//                statisticRepo.save(statisticDTO);
//            } else {
//                logger.error("FAIL SEND STATISTIC DATA");
//            }
        }
    }
}
