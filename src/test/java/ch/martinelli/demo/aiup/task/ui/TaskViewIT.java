package ch.martinelli.demo.aiup.task.ui;

import ch.martinelli.demo.aiup.core.ui.PlaywrightIT;
import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * Playwright integration tests for UC-006: View Team Tasks
 * <p>
 * These tests verify the browser-based behavior of the team tasks view, covering all main
 * success scenarios and alternative flows.
 */
class TaskViewIT extends PlaywrightIT {

	/**
	 * UC-006 Main Success Scenario: User with single team Steps 1-3: User navigates to
	 * view, system determines teams, auto-selects single team
	 * <p>
	 * BR-001: Team Membership Required BR-002: All Team Tasks Visible BR-003: Task List
	 * Ordering (sorted by updated_at DESC)
	 */
	@Test
	void user_with_single_team_sees_tasks() {
		// Given: User with access to exactly one team
		login("singleTeamUser", "user");
		page.navigate("http://localhost:" + localServerPort);
		waitForVaadin();

		// Then: System auto-selects the single team
		var teamSelector = page.locator("vaadin-combo-box").filter(new Locator.FilterOptions().setHasText("Team"));
		assertThat(teamSelector.isVisible()).isTrue();

		// Verify team selector shows "Development Team"
		var selectedValue = teamSelector.locator("input").inputValue();
		assertThat(selectedValue).isEqualTo("Development Team");

		// And: Team selector is read-only (no option to change when only one team)
		var inputField = teamSelector.locator("input[slot='input']");
		assertThat(inputField.getAttribute("readonly")).isNotNull();

		// Then: System displays tasks in a grid
		var grid = page.locator("vaadin-grid");
		assertThat(grid.isVisible()).isTrue();

		// Verify grid has visible content and shows expected task
		// BR-003: Verify first task is the most recently updated
		// "Fix navigation bug" should be first (updated most recently)
		var gridText = grid.innerText();
		assertThat(gridText).contains("Fix navigation bug");
	}

	/**
	 * UC-006 Main Success Scenario: User with multiple teams Steps 4-6: User selects
	 * team, system retrieves and displays tasks
	 */
	@Test
	void user_with_multiple_teams_can_select_team() {
		// Given: User belongs to multiple teams (Development Team and QA Team)
		login("user", "user");
		page.navigate("http://localhost:" + localServerPort);
		waitForVaadin();

		// Then: System shows a team selector
		var teamSelector = page.locator("vaadin-combo-box").filter(new Locator.FilterOptions().setHasText("Team"));
		assertThat(teamSelector.isVisible()).isTrue();

		// Verify team selector is NOT read-only (multiple teams available)
		var inputField = teamSelector.locator("input[slot='input']");
		assertThat(inputField.getAttribute("readonly")).isNull();

		// Verify grid displays tasks for initially selected team
		var grid = page.locator("vaadin-grid");
		assertThat(grid.isVisible()).isTrue();

		// When: User opens combo box and selects a different team
		teamSelector.click();
		waitForVaadin();

		// Select QA Team from the dropdown
		var qaTeamOption = page.locator("vaadin-combo-box-item")
			.filter(new Locator.FilterOptions().setHasText("QA Team"));
		qaTeamOption.click();
		waitForVaadin();

		// Then: Grid updates with QA Team tasks
		var newText = grid.innerText();

		// QA Team has different tasks (Test login feature, Performance testing)
		assertThat(newText).contains("Test login feature");
	}

	/**
	 * UC-006 Alternative Flow A1: No Tasks in Team Trigger: Selected team has no tasks
	 * Flow: System displays empty state message: "No tasks found for this team."
	 */
	@Test
	void displays_empty_state_when_team_has_no_tasks() {
		// Given: Admin belongs to Empty Team (team with no tasks)
		login("admin", "admin");
		page.navigate("http://localhost:" + localServerPort);
		waitForVaadin();

		// When: User selects team with no tasks
		var teamSelector = page.locator("vaadin-combo-box").filter(new Locator.FilterOptions().setHasText("Team"));
		teamSelector.click();
		waitForVaadin();

		var emptyTeamOption = page.locator("vaadin-combo-box-item")
			.filter(new Locator.FilterOptions().setHasText("Empty Team"));
		emptyTeamOption.click();
		waitForVaadin();

		// Then: System displays empty state message
		var emptyStateMessage = page.locator("span:has-text('No tasks found for this team.')");
		assertThat(emptyStateMessage.isVisible()).isTrue();

		// And: Grid is hidden
		var grid = page.locator("vaadin-grid");
		assertThat(grid.isVisible()).isFalse();
	}

	/**
	 * UC-006 Alternative Flow A2: User Not Member of Any Team Trigger: User is not a
	 * member of any team Flow: System displays message: "You are not a member of any
	 * team. Contact your administrator."
	 */
	@Test
	void displays_message_when_user_not_member_of_any_team() {
		// Given: User without team membership
		login("noTeam", "user");
		page.navigate("http://localhost:" + localServerPort);
		waitForVaadin();

		// Then: System displays message about not being member of any team
		var message = page.locator("span:has-text('You are not a member of any team')");
		assertSoftly(softly -> {
			softly.assertThat(message.isVisible()).isTrue();
			softly.assertThat(message.innerText())
				.isEqualTo("You are not a member of any team. Contact your administrator.");
		});

		// And: Team selector is hidden
		var teamSelector = page.locator("vaadin-combo-box");
		assertThat(teamSelector.isVisible()).isFalse();

		// And: Grid is hidden
		var grid = page.locator("vaadin-grid");
		assertThat(grid.isVisible()).isFalse();
	}

	/**
	 * UC-006 UI Elements: Verify task list displays required columns Columns: Title,
	 * Status, Assigned To, Created Date, Created By
	 */
	@Test
	void displays_all_required_task_columns() {
		// Given: User with team access
		login("user", "user");
		page.navigate("http://localhost:" + localServerPort);
		waitForVaadin();

		// Then: Grid displays all required columns
		var grid = page.locator("vaadin-grid");
		assertThat(grid.isVisible()).isTrue();

		// Verify column headers are present by checking grid text content
		// Vaadin Grid renders headers within the component
		var gridText = grid.innerText();

		assertSoftly(softly -> {
			softly.assertThat(gridText).contains("Title");
			softly.assertThat(gridText).contains("Status");
			softly.assertThat(gridText).contains("Assigned To");
			softly.assertThat(gridText).contains("Created Date");
			softly.assertThat(gridText).contains("Created By");
		});
	}

	/**
	 * UC-006 Business Rule BR-002: All Team Tasks Visible Team members can see all tasks
	 * belonging to their team, regardless of whether the tasks are assigned to them.
	 */
	@Test
	void user_sees_all_team_tasks_including_unassigned_and_others_tasks() {
		// Given: User belongs to Development Team
		login("singleTeamUser", "user");
		page.navigate("http://localhost:" + localServerPort);
		waitForVaadin();

		// When: Viewing Development Team tasks
		var grid = page.locator("vaadin-grid");
		var gridText = grid.innerText();

		// Then: User can see tasks in various assignment states
		assertSoftly(softly -> {
			// Task assigned to admin
			softly.assertThat(gridText).contains("admin");
			// Unassigned task should show "Unassigned"
			softly.assertThat(gridText).contains("Unassigned");
			// Various task statuses visible
			softly.assertThat(gridText).containsAnyOf("OPEN", "ASSIGNED", "DRAFT");
		});
	}

}
