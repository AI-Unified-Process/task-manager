package ch.martinelli.demo.aiup.core.ui;

import ch.martinelli.demo.aiup.TestcontainersConfiguration;
import ch.martinelli.demo.aiup.core.ui.po.LoginPO;
import com.microsoft.playwright.*;
import in.virit.mopo.Mopo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class PlaywrightIT {

	@LocalServerPort
	protected Integer localServerPort;

	private static Playwright playwright;

	private static Browser browser;

	protected Page page;

	private BrowserContext browserContext;

	protected Mopo mopo;

	@BeforeAll
	static void setUpClass() {
		playwright = Playwright.create();
		BrowserType browserType = playwright.chromium();
		BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions();
		// set to false if you want to see the browser during development
		launchOptions.headless = false;
		browser = browserType.launch(launchOptions);
	}

	@AfterAll
	static void tearDownClass() {
		browser.close();
		playwright.close();
	}

	@BeforeEach
	void setUp() {
		browserContext = browser.newContext();
		page = browserContext.newPage();
		mopo = new Mopo(page);
	}

	@AfterEach
	void tearDown() {
		page.close();
		browserContext.close();
	}

	protected void login(String username, String password) {
		// Navigate to the login view
		page.navigate("http://localhost:" + localServerPort + "/login");

		// Wait for the login page to load
		page.waitForLoadState();

		// Login with provided username and password
		var loginPO = new LoginPO(page);
		loginPO.login(username, password);

		// Wait for navigation after login
		page.waitForLoadState();

		// Wait for Vaadin client-server connection to settle
		mopo.waitForConnectionToSettle();
	}

	/**
	 * Wait for Vaadin client-server connection to settle. This is more reliable than
	 * arbitrary timeouts.
	 */
	protected void waitForVaadin() {
		mopo.waitForConnectionToSettle();
	}

	/**
	 * Logout by clearing browser cookies and navigating to the login page. This is more
	 * reliable than navigating to /logout which requires a POST request.
	 */
	protected void logout() {
		browserContext.clearCookies();
		page.navigate("http://localhost:" + localServerPort + "/login");
		page.waitForLoadState();
	}

}
