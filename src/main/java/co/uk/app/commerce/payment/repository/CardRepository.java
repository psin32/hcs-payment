package co.uk.app.commerce.payment.repository;

import java.util.Collection;

import org.springframework.data.mongodb.repository.MongoRepository;

import co.uk.app.commerce.payment.document.CardDetails;

public interface CardRepository extends MongoRepository<CardDetails, String> {

	CardDetails findByUserIdAndToken(String userId, String token);

	Collection<CardDetails> findByUserId(String userId);

	CardDetails findByUserIdAndCardId(String userId, String cardId);
}
