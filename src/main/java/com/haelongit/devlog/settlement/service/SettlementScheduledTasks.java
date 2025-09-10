package com.haelongit.devlog.settlement.service;

import com.haelongit.devlog.payment.entity.Payment;
import com.haelongit.devlog.payment.repository.PaymentRepository;
import com.haelongit.devlog.settlement.entity.Settlement;
import com.haelongit.devlog.settlement.reposiitory.SettlementRepository;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SettlementScheduledTasks(PaymentRepository paymentRepository, SettlementRepository settlementRepository, JdbcTemplate jdbcTemplate) {
        this.paymentRepository = paymentRepository;
        this.settlementRepository = settlementRepository;
        this.jdbcTemplate = jdbcTemplate;
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
        bulkProcessSettlements(settlementMap, yesterday);   // 결제 내역 집계하기
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
        // 1. 개별 처리를 사용해서, 집계를 하는 경우, 실행 시간(ms) : 1702
//        settlementMap.entrySet().stream()
//                .forEach(entry -> {
//                    Settlement settlement = Settlement.create(entry.getKey(), entry.getValue(), paymentDate);
//                    settlementRepository.save(settlement);
//                });

        // 2. 병렬 처리를 사용해서, 집계 성능 개선한 경우, 실행 시간(ms) : 1026
        // 데드락 방지를 위해, ForkJoinPool 사용 - 별도의 독립적인 스레드 풀 사용
        ForkJoinPool customForkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());

        try {
            customForkJoinPool.submit(() ->
                    settlementMap.entrySet().parallelStream()
                            .forEach(entry -> {
                                Settlement settlement = Settlement.create(entry.getKey(), entry.getValue(), paymentDate);
                                settlementRepository.save(settlement);
                            })
            ).get();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            customForkJoinPool.shutdown();
        }
    }

    // 3. burk Insert 방식을 사용해서 집계 성능 개선한 경우, 실행 시간(ms) : 315
    private void bulkProcessSettlements(Map<Long, BigDecimal> settlementMap, LocalDate paymentDate) {
        // 1번 방식
//        String sql = "INSERT INTO settlements (partner_id, total_amount, payment_date) VALUES (?, ?, ?)";
//
//        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                Long partnerId = (Long) settlementMap.keySet().toArray()[i];
//                BigDecimal amount = settlementMap.get(partnerId);
//
//                ps.setLong(1, partnerId);
//                ps.setBigDecimal(2, amount);
//                ps.setObject(3, paymentDate);
//            }
//
//            @Override
//            public int getBatchSize() {
//                return settlementMap.size();
//            }
//        });

        // 2번 방식 : 1번 + 예외 처리
        String sql = "INSERT INTO settlements (partner_id, total_amount, payment_date) VALUES (?, ?, ?)";
        List<Long> failedPartnerIds = new ArrayList<>(); // 실패한 partnerId를 저장할 리스트 선언

        try {
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Long partnerId = (Long) settlementMap.keySet().toArray()[i];
                    BigDecimal amount = settlementMap.get(partnerId);

                    ps.setLong(1, partnerId);
                    ps.setBigDecimal(2, amount);
                    ps.setObject(3, paymentDate);
                }

                @Override
                public int getBatchSize() {
                return settlementMap.size();
                }
            });
        } catch (DataAccessException e) {
            Throwable cause = e.getCause();
            if (cause instanceof BatchUpdateException) {
                BatchUpdateException batchEx = (BatchUpdateException) cause;
                int[] updateCounts = batchEx.getUpdateCounts();

                // 실패한 인덱스만 추출
                int index = 0;
                for (Map.Entry<Long, BigDecimal> entry : settlementMap.entrySet()) {
                    if (updateCounts[index] == Statement.EXECUTE_FAILED) { // 실패한 경우
                        failedPartnerIds.add(entry.getKey());
                    }
                    index++;
                }

                // 로그 또는 추가적인 후속 처리
                notifyError(failedPartnerIds);
            }
            throw e; // 실패 예외를 다시 던져 호출자에게 알림
        }
    }

    private void notifyError(List<Long> failedPartnerIds) {
        // 에러 로그 기록
        log.error("Failed to process the following partner IDs: {}", failedPartnerIds);

        // 상세한 로그 메시지 추가
        failedPartnerIds.forEach(id -> log.debug("Failed partner ID: {}", id));

        // 담당자 알림(email ,webhook)
        // 정산 실패 케이스를 DB에 저장
    }
}
