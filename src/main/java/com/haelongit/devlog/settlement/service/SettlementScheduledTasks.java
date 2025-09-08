package com.haelongit.devlog.settlement.service;

import com.haelongit.devlog.payment.entity.Payment;
import com.haelongit.devlog.payment.repository.PaymentRepository;
import com.haelongit.devlog.settlement.entity.Settlement;
import com.haelongit.devlog.settlement.reposiitory.SettlementRepository;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SettlementScheduledTasks {

    public static final String PAYMENT_COMPLETED = "paid";

    private final PaymentRepository paymentRepository;

    private final SettlementRepository settlementRepository;

    @Autowired
    public SettlementScheduledTasks(PaymentRepository paymentRepository, SettlementRepository settlementRepository) {
        this.paymentRepository = paymentRepository;
        this.settlementRepository = settlementRepository;
    }

    // 1분마다 실행(어떤 시점에 스케줄링을 돌릴건지) - 개발하면서 집계 기능 확인을 위해, 1분 단위로 설정
    @Scheduled(cron = "0 * * * * ?")
//    @Scheduled(cron = "0 35 16 * * ?")      // 특정 시간에 스케줄링 사용
    @SchedulerLock(name = "ScheduledTask_run")      // 스케줄링 메서드에 lock 설정
    public void dailySettlement() {
        // 스케줄링 로직 추가
        // 어제의 날짜를 가져옴
        LocalDate yesterday = LocalDate.now().minusDays(1);
        // 어제의 시작 시각 설정 (2024-10-26 00:00:00)
        LocalDateTime startDate = yesterday.atStartOfDay();
        // 어제의 끝 시각 설정 (2024-10-26 23:59:59)
        LocalDateTime endDate = yesterday.atTime(LocalTime.of(23, 59, 59));

        // 해당 기간 동안의 결제 내역 조회 및 집계
        Map<Long, BigDecimal> settlementMap = getSettlementMap(startDate, endDate);

        long beforeTime1 = System.currentTimeMillis();
        processSettlements(settlementMap, yesterday);
        long afterTime1 = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        long diffTime1 = afterTime1 - beforeTime1; // 두 개의 실행 시간
        log.info("실행 시간(ms): " + diffTime1); // 세컨드(초 단위 변환)
    }

    private Map<Long, BigDecimal> getSettlementMap(LocalDateTime startDate, LocalDateTime endDate) {
        List<Payment> paymentList = paymentRepository.findByPaymentDateBetweenAndStatus(startDate, endDate, PAYMENT_COMPLETED);
        // partner_id를 기준으로 group by
        return paymentList.stream()
                .collect(Collectors.groupingBy(
                        Payment::getPartnerId,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                Payment::getPaymentAmount,
                                BigDecimal::add
                        )
                ));
    }

    private void processSettlements(Map<Long, BigDecimal> settlementMap, LocalDate paymentDate) {
        settlementMap.entrySet().parallelStream()
                .forEach(entry -> {
                    Settlement settlement = Settlement.create(entry.getKey(), entry.getValue(), paymentDate);
                    settlementRepository.save(settlement);
                });
    }
}
