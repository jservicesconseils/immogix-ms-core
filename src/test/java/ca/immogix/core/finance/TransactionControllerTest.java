package ca.immogix.core.finance;

import ca.immogix.core.finance.dto.CreateTransactionRequest;
import ca.immogix.core.finance.dto.TransactionResponse;
import ca.immogix.core.property.PropertyService;
import ca.immogix.core.property.dto.PropertyResponse;
import ca.immogix.core.settings.dto.OptionRef;
import ca.immogix.core.settings.propertystatus.dto.PropertyStatusOptionResponse;
import ca.immogix.core.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@Import(SecurityConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private PropertyService propertyService;

    @MockBean
    private JwtDecoder jwtDecoder;

    private static PropertyResponse propertyResponse(UUID id, UUID organizationId) {
        return new PropertyResponse(id, organizationId, new OptionRef(UUID.randomUUID(), "Single-family home", "Maison individuelle"), null,
                "123 rue des Lilas", "Montreal", "QC", "H1A 1A1",
                new PropertyStatusOptionResponse.PropertyStatusRef(UUID.randomUUID(), "Vacant", "Vacante", "warn"),
                null, null, null, null, null, null, null, null, Instant.now());
    }

    @Test
    void creerUneTransaction_tenantCorrespondant_renvoie201() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        when(propertyService.getById(propertyId)).thenReturn(propertyResponse(propertyId, organizationId));
        when(transactionService.create(org.mockito.ArgumentMatchers.eq(propertyId), any())).thenReturn(
                new TransactionResponse(transactionId, propertyId, TransactionType.DEPENSE, TransactionCategory.ENTRETIEN,
                        new BigDecimal("250.00"), "Reparation plomberie", LocalDate.of(2026, 1, 15), Instant.now()));

        CreateTransactionRequest request = new CreateTransactionRequest(
                TransactionType.DEPENSE, TransactionCategory.ENTRETIEN, new BigDecimal("250.00"), "Reparation plomberie", LocalDate.of(2026, 1, 15));

        mockMvc.perform(post("/api/v1/properties/{propertyId}/transactions", propertyId)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/properties/" + propertyId + "/transactions/" + transactionId))
                .andExpect(jsonPath("$.category").value("ENTRETIEN"));
    }

    @Test
    void creerUneTransaction_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        when(propertyService.getById(propertyId)).thenReturn(propertyResponse(propertyId, organizationId));

        CreateTransactionRequest request = new CreateTransactionRequest(
                TransactionType.REVENU, TransactionCategory.LOYER, new BigDecimal("1500.00"), null, LocalDate.of(2026, 1, 1));

        mockMvc.perform(post("/api/v1/properties/{propertyId}/transactions", propertyId)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void lireUneTransaction_inexistante_renvoie404() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();
        when(propertyService.getById(propertyId)).thenReturn(propertyResponse(propertyId, organizationId));
        when(transactionService.getById(propertyId, transactionId)).thenThrow(new TransactionNotFoundException(transactionId));

        mockMvc.perform(get("/api/v1/properties/{propertyId}/transactions/{id}", propertyId, transactionId)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void listerLesTransactions_tenantCorrespondant_renvoie200() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID propertyId = UUID.randomUUID();
        when(propertyService.getById(propertyId)).thenReturn(propertyResponse(propertyId, organizationId));
        when(transactionService.listByProperty(propertyId)).thenReturn(java.util.List.of(
                new TransactionResponse(UUID.randomUUID(), propertyId, TransactionType.REVENU, TransactionCategory.LOYER,
                        new BigDecimal("1500.00"), null, LocalDate.of(2026, 1, 1), Instant.now())));

        mockMvc.perform(get("/api/v1/properties/{propertyId}/transactions", propertyId)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("REVENU"));
    }
}
