package ca.immogix.core.settings.propertystatus;

import ca.immogix.core.security.SecurityConfig;
import ca.immogix.core.settings.SettingsItemNotFoundException;
import ca.immogix.core.settings.propertystatus.dto.CreatePropertyStatusOptionRequest;
import ca.immogix.core.settings.propertystatus.dto.PropertyStatusOptionResponse;
import ca.immogix.core.settings.propertystatus.dto.UpdatePropertyStatusOptionRequest;
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
@WebMvcTest(PropertyStatusOptionController.class)
@Import(SecurityConfig.class)
class PropertyStatusOptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PropertyStatusOptionService propertyStatusOptionService;

    @MockBean
    private JwtDecoder jwtDecoder;

    private static CreatePropertyStatusOptionRequest createRequest(UUID organizationId) {
        return new CreatePropertyStatusOptionRequest(organizationId, "Active", "Actif", "Propriete geree activement", "success");
    }

    private static PropertyStatusOptionResponse response(UUID id, UUID organizationId) {
        return new PropertyStatusOptionResponse(id, organizationId, "Active", "Actif", "Propriete geree activement", "success", Instant.now());
    }

    @Test
    void creerUnStatutDePropriete_tenantCorrespondant_renvoie201() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyStatusOptionService.create(any())).thenReturn(response(id, organizationId));

        mockMvc.perform(post("/api/v1/property-statuses")
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest(organizationId))))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/property-statuses/" + id))
                .andExpect(jsonPath("$.nameFr").value("Actif"));
    }

    @Test
    void creerUnStatutDePropriete_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/property-statuses")
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest(organizationId))))
                .andExpect(status().isForbidden());
    }

    @Test
    void creerUnStatutDePropriete_sansJeton_renvoie401() throws Exception {
        mockMvc.perform(post("/api/v1/property-statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest(UUID.randomUUID()))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void creerUnStatutDePropriete_nomVide_renvoie400() throws Exception {
        UUID organizationId = UUID.randomUUID();
        CreatePropertyStatusOptionRequest invalide = new CreatePropertyStatusOptionRequest(organizationId, "", "", null, "success");

        mockMvc.perform(post("/api/v1/property-statuses")
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalide)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void lireUnStatutDePropriete_tenantCorrespondant_renvoie200() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyStatusOptionService.getById(id)).thenReturn(response(id, organizationId));

        mockMvc.perform(get("/api/v1/property-statuses/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void lireUnStatutDePropriete_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyStatusOptionService.getById(id)).thenReturn(response(id, organizationId));

        mockMvc.perform(get("/api/v1/property-statuses/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void lireUnStatutDePropriete_inexistant_renvoie404() throws Exception {
        UUID id = UUID.randomUUID();
        when(propertyStatusOptionService.getById(id)).thenThrow(new SettingsItemNotFoundException("statut de propriete", id));

        mockMvc.perform(get("/api/v1/property-statuses/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", UUID.randomUUID().toString()))))
                .andExpect(status().isNotFound());
    }

    @Test
    void listerLesStatutsDeProprieteDUneOrganisation_tenantCorrespondant_renvoie200() throws Exception {
        UUID organizationId = UUID.randomUUID();
        when(propertyStatusOptionService.listByOrganization(organizationId)).thenReturn(List.of(response(UUID.randomUUID(), organizationId)));

        mockMvc.perform(get("/api/v1/property-statuses")
                        .param("organizationId", organizationId.toString())
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nameFr").value("Actif"));
    }

    @Test
    void listerLesStatutsDeProprieteDUneOrganisation_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/property-statuses")
                        .param("organizationId", organizationId.toString())
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void modifierUnStatutDePropriete_tenantCorrespondant_renvoie200() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyStatusOptionService.getById(id)).thenReturn(response(id, organizationId));
        when(propertyStatusOptionService.update(eq(id), any())).thenReturn(response(id, organizationId));

        UpdatePropertyStatusOptionRequest request = new UpdatePropertyStatusOptionRequest("Sold", "Vendu", "Propriete vendue", "secondary");

        mockMvc.perform(put("/api/v1/property-statuses/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void modifierUnStatutDePropriete_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyStatusOptionService.getById(id)).thenReturn(response(id, organizationId));

        UpdatePropertyStatusOptionRequest request = new UpdatePropertyStatusOptionRequest("Sold", "Vendu", "Propriete vendue", "secondary");

        mockMvc.perform(put("/api/v1/property-statuses/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString())))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void supprimerUnStatutDePropriete_tenantCorrespondant_renvoie204() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyStatusOptionService.getById(id)).thenReturn(response(id, organizationId));

        mockMvc.perform(delete("/api/v1/property-statuses/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", organizationId.toString()))))
                .andExpect(status().isNoContent());
    }

    @Test
    void supprimerUnStatutDePropriete_tenantDifferent_renvoie403() throws Exception {
        UUID organizationId = UUID.randomUUID();
        UUID autreTenant = UUID.randomUUID();
        UUID id = UUID.randomUUID();
        when(propertyStatusOptionService.getById(id)).thenReturn(response(id, organizationId));

        mockMvc.perform(delete("/api/v1/property-statuses/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", autreTenant.toString()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void supprimerUnStatutDePropriete_inexistant_renvoie404() throws Exception {
        UUID id = UUID.randomUUID();
        when(propertyStatusOptionService.getById(id)).thenThrow(new SettingsItemNotFoundException("statut de propriete", id));

        mockMvc.perform(delete("/api/v1/property-statuses/{id}", id)
                        .with(jwt().jwt(builder -> builder.claim("tenant_id", UUID.randomUUID().toString()))))
                .andExpect(status().isNotFound());
    }
}
