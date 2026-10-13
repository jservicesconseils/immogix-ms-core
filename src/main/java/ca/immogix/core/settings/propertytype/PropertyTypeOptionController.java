package ca.immogix.core.settings.propertytype;

import ca.immogix.core.security.TenantClaims;
import ca.immogix.core.settings.propertytype.dto.CreatePropertyTypeOptionRequest;
import ca.immogix.core.settings.propertytype.dto.PropertyTypeOptionResponse;
import ca.immogix.core.settings.propertytype.dto.UpdatePropertyTypeOptionRequest;
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
public class PropertyTypeOptionController {

    private final PropertyTypeOptionService propertyTypeOptionService;

    public PropertyTypeOptionController(PropertyTypeOptionService propertyTypeOptionService) {
        this.propertyTypeOptionService = propertyTypeOptionService;
    }

    @PostMapping("/api/v1/property-types")
    public ResponseEntity<PropertyTypeOptionResponse> create(@Valid @RequestBody CreatePropertyTypeOptionRequest request,
                                                               @AuthenticationPrincipal Jwt jwt) {
        TenantClaims.from(jwt).assertAccessTo(request.organizationId());
        PropertyTypeOptionResponse created = propertyTypeOptionService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/property-types/" + created.id())).body(created);
    }

    @GetMapping("/api/v1/property-types/{id}")
    public ResponseEntity<PropertyTypeOptionResponse> getById(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        PropertyTypeOptionResponse propertyTypeOption = propertyTypeOptionService.getById(id);
        TenantClaims.from(jwt).assertAccessTo(propertyTypeOption.organizationId());
        return ResponseEntity.ok(propertyTypeOption);
    }

    @GetMapping("/api/v1/property-types")
    public ResponseEntity<List<PropertyTypeOptionResponse>> listByOrganization(@RequestParam UUID organizationId,
                                                                                 @AuthenticationPrincipal Jwt jwt) {
        TenantClaims.from(jwt).assertAccessTo(organizationId);
        return ResponseEntity.ok(propertyTypeOptionService.listByOrganization(organizationId));
    }

    @PutMapping("/api/v1/property-types/{id}")
    public ResponseEntity<PropertyTypeOptionResponse> update(@PathVariable UUID id,
                                                               @Valid @RequestBody UpdatePropertyTypeOptionRequest request,
                                                               @AuthenticationPrincipal Jwt jwt) {
        PropertyTypeOptionResponse existing = propertyTypeOptionService.getById(id);
        TenantClaims.from(jwt).assertAccessTo(existing.organizationId());
        return ResponseEntity.ok(propertyTypeOptionService.update(id, request));
    }

    @DeleteMapping("/api/v1/property-types/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        PropertyTypeOptionResponse existing = propertyTypeOptionService.getById(id);
        TenantClaims.from(jwt).assertAccessTo(existing.organizationId());
        propertyTypeOptionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
