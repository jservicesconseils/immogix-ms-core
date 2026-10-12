package ca.immogix.core.admin;

import ca.immogix.core.admin.dto.AuditLogResponse;
import ca.immogix.core.security.TenantClaims;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/{organizationId}/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> list(@PathVariable UUID organizationId, @AuthenticationPrincipal Jwt jwt) {
        TenantClaims.from(jwt).assertAccessTo(organizationId);
        return ResponseEntity.ok(auditLogService.listForOrganization(organizationId));
    }
}
