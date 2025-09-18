package com.haelongit.devlog.payment.controller;

import com.haelongit.devlog.payment.dto.request.PaymentSaveRequestDto;
import com.haelongit.devlog.payment.dto.request.PrepareRequestDto;
import com.haelongit.devlog.payment.dto.response.PrepareResponseDto;
import com.haelongit.devlog.payment.entity.Payment;
import com.haelongit.devlog.payment.service.PaymentService;
import com.haelongit.devlog.user.entity.User;
import com.haelongit.devlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository; // User 정보를 가져오기 위해 주입

    /**
     * 결제 준비 API: 프론트엔드에서 PG사 SDK를 호출하기 전에 필요한 정보를 생성하여 전달
     */
    @PostMapping("/prepare")
    public ResponseEntity<PrepareResponseDto> preparePayment(
            @RequestBody PrepareRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 현재 로그인한 사용자의 ID를 가져옴
        User currentUser = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Payment preparedPayment = paymentService.preparePayment(currentUser.getId(), requestDto.amount());

        return ResponseEntity.ok(new PrepareResponseDto(preparedPayment));
    }

    /**
     * 결제 완료 후 PG사로부터 웹훅(Webhook)을 수신하는 API (로직 변경)
     */
    @PostMapping("/portone")
    public ResponseEntity<String> savePortoneWebhook(@RequestBody PaymentSaveRequestDto paymentRequest) {
        log.info("====== PortOne Webhook Received! Merchant UID: {} ======", paymentRequest.getMerchantUid());
        try {
            // 이제 서비스 계층의 processWebhook 메서드 하나만 호출하면 됩니다.
            paymentService.processWebhook(paymentRequest);
            return ResponseEntity.ok("Webhook processed successfully.");
        } catch (IllegalArgumentException e) {
            // merchant_uid를 찾을 수 없는 등, 유효하지 않은 요청 데이터에 대한 처리
            log.error("Invalid webhook data: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // 그 외 모든 예상치 못한 서버 오류
            log.error("Error processing payment webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process webhook.");
        }
    }


    /**
     * 모든 결제 내역 조회 (변경 없음, 단, SecurityConfig에 의해 로그인한 사용자만 접근 가능)
     */
    @GetMapping("/list")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    /**
     * 결제 취소 (GET -> POST로 변경 권장)
     * 데이터를 변경하는 작업이므로 GET보다는 POST가 더 표준적인 방식입니다.
     */
    @PostMapping("/cancel/{uid}") // @GetMapping에서 @PostMapping으로 변경
    public ResponseEntity<String> cancelPayment(@PathVariable("uid")String uid) {
        paymentService.canclePayment(uid);
        return ResponseEntity.ok("Payment cancellation processed successfully.");
    }
}
