package info.puntanegra.fhir.server.repository;

import info.puntanegra.fhir.server.domain.PersistentAuditEvent;

import java.util.List;

import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;

/**
 * Spring Data MongoDB repository for the PersistentAuditEvent entity.
 */
@Service
public class PersistenceAuditEventRepository {

	public List<PersistentAuditEvent> findByPrincipal(String principal) {
		return null;
	}

	public List<PersistentAuditEvent> findByPrincipalAndAuditEventDateAfter(String principal, LocalDateTime after) {
		return null;
	}

	public List<PersistentAuditEvent> findAllByAuditEventDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
		return null;
	}

	public Iterable<PersistentAuditEvent> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(PersistentAuditEvent persistentAuditEvent) {
		// TODO Auto-generated method stub
		
	}
}
