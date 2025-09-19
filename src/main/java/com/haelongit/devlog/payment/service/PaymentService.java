package com.haelongit.devlog.payment.service;

import com.haelongit.devlog.payment.dto.request.PaymentSaveRequestDto;
import com.haelongit.devlog.payment.entity.Payment;
import com.haelongit.devlog.payment.repository.PaymentRepository;
import com.haelongit.devlog.payment.util.PaymentClient;
import com.haelongit.devlog.user.entity.User;
import com.haelongit.devlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor // @Autowired 생성자 대신 사용 (더 권장되는 방식)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;
    private final UserRepository userRepository; // UserRepository 주입

    /**
     * 결제 준비: 프론트엔드에서 결제 요청 전, 주문 정보를 생성
     */
    @Transactional
    public Payment preparePayment(Long userId, BigDecimal amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found for ID: " + userId));

        // 고유한 주문번호(merchant_uid) 생성
        String merchantUid = "order_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);

        Payment payment = Payment.builder()
                .user(user)
                .paymentAmount(amount)
                .status("READY") // 결제 대기 상태로 생성
                .merchantUid(merchantUid)
                // 임시 주문 ID, 실제로는 Order 테이블과 연관관계 필요
                .orderId(System.currentTimeMillis())
                .paymentDate(LocalDateTime.now()) // paymentDate 필드 추가
                .partnerId(user.getId()) // <<< 여기에 partnerId를 user의 ID로 설정합니다.
                .build();

        return paymentRepository.save(payment);
    }

    /**
     * 웹훅 수신 후 처리 로직
     */
    @Transactional
    public void processWebhook(PaymentSaveRequestDto webhookDto) {
        // 1. 웹훅으로 받은 merchant_uid를 사용해 우리 DB에서 기존 "READY" 상태의 결제 정보를 찾습니다.
        Payment payment = paymentRepository.findByMerchantUid(webhookDto.getMerchantUid())
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for merchantUid: " + webhookDto.getMerchantUid()));

        // 2. (선택사항/실서버용) 아임포트 서버에 결제 정보를 직접 조회하여 금액 위변조 여부 검증
        // paymentClient.verifyPayment(webhookDto.getImpUid(), payment.getPaymentAmount());

        // 3. 결제가 성공적으로 검증되면, "READY" 상태였던 Payment 엔티티의 상태를 "PAID"로 바꾸고,
        //    웹훅으로 받은 상세 정보(imp_uid, pg_tid 등)를 채워줍니다.
        payment.setStatus("PAID");
        payment.setImpUid(webhookDto.getImpUid());
        payment.setPgTid(webhookDto.getPgTid());
        payment.setPaymentMethod(webhookDto.getPayMethod());
        // ... 필요한 다른 정보들도 업데이트 ...

        // 4. 해당 결제에 연결된 사용자의 구독 기간을 연장합니다.
        User user = payment.getUser();
        user.extendSubscription(30);

        // @Transactional 어노테이션에 의해, 메서드가 끝나면 변경된 payment와 user 정보가 자동으로 DB에 저장됩니다.
    }

    /**
     * 모든 결제 내역 조회
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * 결제 취소
     */
    @Transactional
    public void canclePayment(String uid) {
        // 외부 API로 결제 취소 요청
        paymentClient.cancelPayment(uid);

        Payment payment = paymentRepository.findByImpUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found with impUid: " + uid));

        // 1. Payment 상태를 "CANCELLED"로 변경
        payment.setStatus("CANCELLED");

        // 2. (중요) 사용자 구독 기간 회수 로직 추가
        User user = payment.getUser();
        // 가장 최근 만료일에서 30일을 빼서 구독 기간을 원상복구
        user.setPaidUntil(user.getPaidUntil().minusDays(30));
        userRepository.save(user);
    }
}
