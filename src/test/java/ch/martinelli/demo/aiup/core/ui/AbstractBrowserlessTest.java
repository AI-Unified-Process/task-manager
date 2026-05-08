package ch.martinelli.demo.aiup.core.ui;

import ch.martinelli.demo.aiup.TestcontainersConfiguration;
import com.vaadin.browserless.SpringBrowserlessTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Locale;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
public abstract class AbstractBrowserlessTest extends SpringBrowserlessTest {

	static {
		Locale.setDefault(Locale.ENGLISH);
	}

}
