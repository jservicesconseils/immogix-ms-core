package ca.immogix.core.finance;

import ca.immogix.core.finance.dto.CreateTransactionRequest;
import ca.immogix.core.finance.dto.TransactionResponse;
import ca.immogix.core.property.Property;
import ca.immogix.core.property.PropertyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository repository;
    private final PropertyService propertyService;

    public TransactionService(TransactionRepository repository, PropertyService propertyService) {
        this.repository = repository;
        this.propertyService = propertyService;
    }

    public TransactionResponse create(UUID propertyId, CreateTransactionRequest request) {
        Property property = propertyService.getEntityById(propertyId);
        Transaction saved = repository.save(new Transaction(
                property, request.type(), request.category(), request.amount(), request.description(), request.transactionDate()));
        return TransactionResponse.from(saved);
    }

    public TransactionResponse getById(UUID propertyId, UUID transactionId) {
        return TransactionResponse.from(getEntityByIdAndProperty(transactionId, propertyId));
    }

    public List<TransactionResponse> listByProperty(UUID propertyId) {
        return repository.findByPropertyId(propertyId).stream()
                .map(TransactionResponse::from)
                .toList();
    }

    private Transaction getEntityByIdAndProperty(UUID transactionId, UUID propertyId) {
        Transaction transaction = repository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        if (!transaction.getProperty().getId().equals(propertyId)) {
            throw new TransactionNotFoundException(transactionId);
        }

        return transaction;
    }
}
