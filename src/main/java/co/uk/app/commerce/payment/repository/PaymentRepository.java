package co.uk.app.commerce.payment.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uk.app.commerce.payment.document.PaymentDetails;

public interface PaymentRepository extends MongoRepository<PaymentDetails, String> {

	PaymentDetails findByOrderId(String orderId);
}
