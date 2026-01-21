package com.example.aqua.payement;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.aqua.Order.Order;
import com.example.aqua.Order.OrderRepository;

import jakarta.transaction.Transactional;



@Service
@Transactional
public class PayementService {
	
	/*implements PaymentService {

    private final  OrderRepository orderRepo ;
    private  final PayementRepository paymentRepo;
    private final FlouciService flouciService;

    public PayementService(
            OrderRepository orderRepo,
            PayementRepository paymentRepo,
            FlouciService flouciService) {
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
        this.flouciService = flouciService;
    }

    @Override
    public PaymentResponseDTO createPayment(Long orderId) {

        // 1. Load order
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // 2. Validate order state
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Order cannot be paid");
        }

        // 3. Create payment (PENDING)
        Payement payment = new Payement();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setProvider("FLOUCI");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepo.save(payment);

        // 4. Lock order
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        orderRepo.save(order);

        // 5. Call Flouci
        FlouciPaymentResult result =
                flouciService.createPayment(
                        payment.getId(),
                        payment.getAmount()
                );

        // 6. Save provider payment id
        payment.setProviderPaymentId(result.getPaymentId());
        paymentRepo.save(payment);

        // 7. Return redirect info
        return new PaymentResponseDTO(
                payment.getId(),
                result.getPaymentUrl()
        );
    }

    @Override
    public void verifyPayment(Long paymentId) {

        // 1. Load payment
        Payement payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // 2. Prevent double processing
        if (payment.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        // 3. Verify with Flouci
        boolean paid =
                flouciService.verifyPayment(
                        payment.getProviderPaymentId()
                );

        Order order = payment.getOrder();

        if (paid) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            order.setStatus(OrderStatus.PAID);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.CANCELLED);
        }

        paymentRepo.save(payment);
      
        
        orderRepo.save(order);
    }  */
}
