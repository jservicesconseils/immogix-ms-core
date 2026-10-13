package ca.immogix.core.settings.propertystatus;

import ca.immogix.core.security.TenantClaims;
import ca.immogix.core.settings.propertystatus.dto.CreatePropertyStatusOptionRequest;
import ca.immogix.core.settings.propertystatus.dto.PropertyStatusOptionResponse;
import ca.immogix.core.settings.propertystatus.dto.UpdatePropertyStatusOptionRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class PropertyStatusOptionController {

    private final PropertyStatusOptionService propertyStatusOptionService;

    public PropertyStatusOptionController(PropertyStatusOptionService propertyStatusOptionService) {
        this.propertyStatusOptionService = propertyStatusOptionService;
    }

    @PostMapping("/api/v1/property-statuses")
    public ResponseEntity<PropertyStatusOptionResponse> create(@Valid @RequestBody CreatePropertyStatusOptionRequest request,
                                                                 @AuthenticationPrincipal Jwt jwt) {
        TenantClaims.from(jwt).assertAccessTo(request.organizationId());
        PropertyStatusOptionResponse created = propertyStatusOptionService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/property-statuses/" + created.id())).body(created);
    }

    @GetMapping("/api/v1/property-statuses/{id}")
    public ResponseEntity<PropertyStatusOptionResponse> getById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        PropertyStatusOptionResponse propertyStatusOption = propertyStatusOptionService.getById(id);
        TenantClaims.from(jwt).assertAccessTo(propertyStatusOption.organizationId());
        return ResponseEntity.ok(propertyStatusOption);
    }

    @GetMapping("/api/v1/property-statuses")
    public ResponseEntity<List<PropertyStatusOptionResponse>> listByOrganization(@RequestParam UUID organizationId,
                                                                                   @AuthenticationPrincipal Jwt jwt) {
        TenantClaims.from(jwt).assertAccessTo(organizationId);
        return ResponseEntity.ok(propertyStatusOptionService.listByOrganization(organizationId));
    }

    @PutMapping("/api/v1/property-statuses/{id}")
    public ResponseEntity<PropertyStatusOptionResponse> update(@PathVariable UUID id,
                                                                 @Valid @RequestBody UpdatePropertyStatusOptionRequest request,
                                                                 @AuthenticationPrincipal Jwt jwt) {
        PropertyStatusOptionResponse existing = propertyStatusOptionService.getById(id);
        TenantClaims.from(jwt).assertAccessTo(existing.organizationId());
        return ResponseEntity.ok(propertyStatusOptionService.update(id, request));
    }

    @DeleteMapping("/api/v1/property-statuses/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        PropertyStatusOptionResponse existing = propertyStatusOptionService.getById(id);
        TenantClaims.from(jwt).assertAccessTo(existing.organizationId());
        propertyStatusOptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
