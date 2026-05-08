package ch.martinelli.demo.aiup.core.ui.po;

import com.microsoft.playwright.Page;

public class LoginPO {

	private final Page page;

	public LoginPO(Page page) {
		this.page = page;
	}

	public void login(String username, String password) {
		// Wait for the login overlay to be attached (LoginView uses LoginOverlay)
		var overlay = page.locator("vaadin-login-overlay[opened]");
		overlay.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
			.setState(com.microsoft.playwright.options.WaitForSelectorState.ATTACHED));

		// Wait a moment for the overlay animation to complete
		try {
			Thread.sleep(500);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		// Fill username - Vaadin LoginOverlay uses input fields
		var usernameField = page.locator("input[name='username']");
		usernameField.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
			.setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
		usernameField.fill(username);

		// Fill password - Vaadin LoginOverlay uses password input
		var passwordField = page.locator("input[name='password']");
		passwordField.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
			.setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
		passwordField.fill(password);

		// Click login button - Vaadin LoginOverlay has a submit button
		var loginButton = page.locator("vaadin-button[slot='submit']");
		loginButton.waitFor(new com.microsoft.playwright.Locator.WaitForOptions()
			.setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE));
		loginButton.click();

		// Wait for navigation after login
		page.waitForLoadState();
	}

}
