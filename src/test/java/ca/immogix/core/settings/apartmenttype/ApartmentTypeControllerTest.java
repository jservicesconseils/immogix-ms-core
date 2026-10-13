package ca.immogix.core.settings.apartmenttype;

import ca.immogix.core.security.SecurityConfig;
import ca.immogix.core.settings.SettingsItemNotFoundException;
import ca.immogix.core.settings.apartmenttype.dto.ApartmentTypeResponse;
import ca.immogix.core.settings.apartmenttype.dto.CreateApartmentTypeRequest;
import ca.immogix.core.settings.apartmenttype.dto.UpdateApartmentTypeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Meme approche que OwnerControllerTest : @WebMvcTest + vraie SecurityConfig, service mocke, JwtDecoder mocke. */
@WebMvcTest(ApartmentTypeController.class)
@Import(SecurityConfig.class)
class ApartmentTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApartmentTypeService apartmentTypeService;

    @MockBean
    private JwtDecoder jwtDecoder;

    private static CreateApartmentTypeRequest createRequest(UUID organizationId) {
        return new CreateApartmentTypeRequest(organizationId, "3.5", "3 1/2", "Deux chambres, un salon");
    }

    private static ApartmentTypeResponse response(UUID id, UUID organizationId) {
        return new ApartmentTypeResponse(id, organizationId, "3.5", "3 1/2", "Deux chambres, un salon", Instant.now());
    }

    @Test
    void creerUnTypeDAppartement_tenantCorrespondant_renvoie201() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(apartmentTypeService.create(any())).thenReturn(response(id, organizationId));

        mockMvc.perform(post("/api/v1/apartment-types")
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest(organizationId))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/apartment-types/" + id))
                .andExpect(jsonPath("$.nameFr").value("3 1/2"));
    }

    @Test
    void creerUnTypeDAppartement_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/apartment-types")
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest(organizationId))))
                .andExpect(status().isForbidden());
    }

    @Test
    void creerUnTypeDAppartement_sansJeton_renvoie401() throws Exception {
        mockMvc.perform(post("/api/v1/apartment-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest(UUID.randomUUID()))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void creerUnTypeDAppartement_nomVide_renvoie400() throws Exception {
        UUID organizationId = UUID.randomUUID();
        CreateApartmentTypeRequest invalide = new CreateApartmentTypeRequest(organizationId, "", "", null);

        mockMvc.perform(post("/api/v1/apartment-types")
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalide)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void lireUnTypeDAppartement_tenantCorrespondant_renvoie200() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(apartmentTypeService.getById(id)).thenReturn(response(id, organizationId));

        mockMvc.perform(get("/api/v1/apartment-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void lireUnTypeDAppartement_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(apartmentTypeService.getById(id)).thenReturn(response(id, organizationId));

        mockMvc.perform(get("/api/v1/apartment-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void lireUnTypeDAppartement_inexistant_renvoie404() throws Exception {
        UUID id = UUID.randomUUID();
        when(apartmentTypeService.getById(id)).thenThrow(new SettingsItemNotFoundException("type d'appartement", id));

        mockMvc.perform(get("/api/v1/apartment-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", UUID.randomUUID().toString()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void listerLesTypesDAppartementDUneOrganisation_tenantCorrespondant_renvoie200() throws Exception {
        UUID organizationId = UUID.randomUUID();
        when(apartmentTypeService.listByOrganization(organizationId)).thenReturn(List.of(response(UUID.randomUUID(), organizationId)));

        mockMvc.perform(get("/api/v1/apartment-types")
                        .param("organizationId", organizationId.toString())
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nameFr").value("3 1/2"));
    }

    @Test
    void listerLesTypesDAppartementDUneOrganisation_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/apartment-types")
                        .param("organizationId", organizationId.toString())
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void modifierUnTypeDAppartement_tenantCorrespondant_renvoie200() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(apartmentTypeService.getById(id)).thenReturn(response(id, organizationId));
        when(apartmentTypeService.update(eq(id), any())).thenReturn(response(id, organizationId));

        UpdateApartmentTypeRequest request = new UpdateApartmentTypeRequest("4.5", "4 1/2", "Trois chambres");

        mockMvc.perform(put("/api/v1/apartment-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void modifierUnTypeDAppartement_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(apartmentTypeService.getById(id)).thenReturn(response(id, organizationId));

        UpdateApartmentTypeRequest request = new UpdateApartmentTypeRequest("4.5", "4 1/2", "Trois chambres");

        mockMvc.perform(put("/api/v1/apartment-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void supprimerUnTypeDAppartement_tenantCorrespondant_renvoie204() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(apartmentTypeService.getById(id)).thenReturn(response(id, organizationId));

        mockMvc.perform(delete("/api/v1/apartment-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString()))))
                .andExpect(status().isNoContent());
    }

    @Test
    void supprimerUnTypeDAppartement_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(apartmentTypeService.getById(id)).thenReturn(response(id, organizationId));

        mockMvc.perform(delete("/api/v1/apartment-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void supprimerUnTypeDAppartement_inexistant_renvoie404() throws Exception {
        UUID id = UUID.randomUUID();
        when(apartmentTypeService.getById(id)).thenThrow(new SettingsItemNotFoundException("type d'appartement", id));

        mockMvc.perform(delete("/api/v1/apartment-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", UUID.randomUUID().toString()))))
                .andExpect(status().isNotFound());
    }
}
