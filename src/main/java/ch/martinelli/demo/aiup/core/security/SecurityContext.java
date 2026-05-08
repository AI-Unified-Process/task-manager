package ch.martinelli.demo.aiup.core.security;

import ch.martinelli.demo.aiup.core.domain.UserDAO;
import ch.martinelli.demo.aiup.db.tables.records.UserRecord;
import com.vaadin.flow.spring.security.AuthenticationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityContext {

	private final AuthenticationContext authenticationContext;

	private final UserDAO userDAO;

	public SecurityContext(AuthenticationContext authenticationContext, UserDAO userDAO) {
		this.userDAO = userDAO;
		this.authenticationContext = authenticationContext;
	}

	public Optional<UserRecord> getLoggedInUser() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()
				|| "anonymousUser".equals(authentication.getName())) {
			return Optional.empty();
		}
		return userDAO.findById(authentication.getName());
	}

	public void logout() {
		authenticationContext.logout();
	}

}
