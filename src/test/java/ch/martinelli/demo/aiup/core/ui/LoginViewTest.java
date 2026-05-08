package ch.martinelli.demo.aiup.core.ui;

import com.vaadin.flow.component.html.H2;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;

@WithAnonymousUser
class LoginViewTest extends AbstractBrowserlessTest {

	@Test
	void navigate_to_login() {
		navigate(LoginView.class);

		var title = $(H2.class).withText("Login").single();
		Assertions.assertThat(title).isNotNull();
	}

}
