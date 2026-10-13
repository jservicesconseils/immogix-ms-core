package ca.immogix.core.settings.propertytype;

import ca.immogix.core.security.SecurityConfig;
import ca.immogix.core.settings.SettingsItemNotFoundException;
import ca.immogix.core.settings.propertytype.dto.CreatePropertyTypeOptionRequest;
import ca.immogix.core.settings.propertytype.dto.PropertyTypeOptionResponse;
import ca.immogix.core.settings.propertytype.dto.UpdatePropertyTypeOptionRequest;
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
@WebMvcTest(PropertyTypeOptionController.class)
@Import(SecurityConfig.class)
class PropertyTypeOptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PropertyTypeOptionService propertyTypeOptionService;

    @MockBean
    private JwtDecoder jwtDecoder;

    private static CreatePropertyTypeOptionRequest createRequest(UUID organizationId) {
        return new CreatePropertyTypeOptionRequest(organizationId, "Residential", "Residentiel", "Immeubles d'habitation");
    }

    private static PropertyTypeOptionResponse response(UUID id, UUID organizationId) {
        return new PropertyTypeOptionResponse(id, organizationId, "Residential", "Residentiel", "Immeubles d'habitation", Instant.now());
    }

    @Test
    void creerUnTypeDePropriete_tenantCorrespondant_renvoie201() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyTypeOptionService.create(any())).thenReturn(response(id, organizationId));

        mockMvc.perform(post("/api/v1/property-types")
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest(organizationId))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/property-types/" + id))
                .andExpect(jsonPath("$.nameFr").value("Residentiel"));
    }

    @Test
    void creerUnTypeDePropriete_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/property-types")
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest(organizationId))))
                .andExpect(status().isForbidden());
    }

    @Test
    void creerUnTypeDePropriete_sansJeton_renvoie401() throws Exception {
        mockMvc.perform(post("/api/v1/property-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest(UUID.randomUUID()))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void creerUnTypeDePropriete_nomVide_renvoie400() throws Exception {
        UUID organizationId = UUID.randomUUID();
        CreatePropertyTypeOptionRequest invalide = new CreatePropertyTypeOptionRequest(organizationId, "", "", null);

        mockMvc.perform(post("/api/v1/property-types")
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalide)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void lireUnTypeDePropriete_tenantCorrespondant_renvoie200() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyTypeOptionService.getById(id)).thenReturn(response(id, organizationId));

        mockMvc.perform(get("/api/v1/property-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void lireUnTypeDePropriete_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyTypeOptionService.getById(id)).thenReturn(response(id, organizationId));

        mockMvc.perform(get("/api/v1/property-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void lireUnTypeDePropriete_inexistant_renvoie404() throws Exception {
        UUID id = UUID.randomUUID();
        when(propertyTypeOptionService.getById(id)).thenThrow(new SettingsItemNotFoundException("type de propriete", id));

        mockMvc.perform(get("/api/v1/property-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", UUID.randomUUID().toString()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void listerLesTypesDeProprieteDUneOrganisation_tenantCorrespondant_renvoie200() throws Exception {
        UUID organizationId = UUID.randomUUID();
        when(propertyTypeOptionService.listByOrganization(organizationId)).thenReturn(List.of(response(UUID.randomUUID(), organizationId)));

        mockMvc.perform(get("/api/v1/property-types")
                        .param("organizationId", organizationId.toString())
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nameFr").value("Residentiel"));
    }

    @Test
    void listerLesTypesDeProprieteDUneOrganisation_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/property-types")
                        .param("organizationId", organizationId.toString())
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void modifierUnTypeDePropriete_tenantCorrespondant_renvoie200() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyTypeOptionService.getById(id)).thenReturn(response(id, organizationId));
        when(propertyTypeOptionService.update(eq(id), any())).thenReturn(response(id, organizationId));

        UpdatePropertyTypeOptionRequest request = new UpdatePropertyTypeOptionRequest("Commercial", "Commercial", "Locaux commerciaux");

        mockMvc.perform(put("/api/v1/property-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void modifierUnTypeDePropriete_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyTypeOptionService.getById(id)).thenReturn(response(id, organizationId));

        UpdatePropertyTypeOptionRequest request = new UpdatePropertyTypeOptionRequest("Commercial", "Commercial", "Locaux commerciaux");

        mockMvc.perform(put("/api/v1/property-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void supprimerUnTypeDePropriete_tenantCorrespondant_renvoie204() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyTypeOptionService.getById(id)).thenReturn(response(id, organizationId));

        mockMvc.perform(delete("/api/v1/property-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString()))))
                .andExpect(status().isNoContent());
    }

    @Test
    void supprimerUnTypeDePropriete_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyTypeOptionService.getById(id)).thenReturn(response(id, organizationId));

        mockMvc.perform(delete("/api/v1/property-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void supprimerUnTypeDePropriete_inexistant_renvoie404() throws Exception {
        UUID id = UUID.randomUUID();
        when(propertyTypeOptionService.getById(id)).thenThrow(new SettingsItemNotFoundException("type de propriete", id));

        mockMvc.perform(delete("/api/v1/property-types/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", UUID.randomUUID().toString()))))
                .andExpect(status().isNotFound());
    }
}
