package ch.martinelli.demo.aiup.core.ui.preferences;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@VaadinSessionScope
@Component
public class UserPreferencesService {

	private @Nullable Long lastSelectedTeamId;

	public Optional<Long> getLastSelectedTeamId() {
		return Optional.ofNullable(lastSelectedTeamId);
	}

	public void setLastSelectedTeamId(@Nullable Long lastSelectedTeamId) {
		this.lastSelectedTeamId = lastSelectedTeamId;
	}

}
