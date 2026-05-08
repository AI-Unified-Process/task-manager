package ch.martinelli.demo.aiup.task.ui;

import ch.martinelli.demo.aiup.core.ui.AbstractBrowserlessTest;
import ch.martinelli.demo.aiup.task.domain.Task;
import ch.martinelli.demo.aiup.team.domain.Team;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

/**
 * Browserless test for UC-006: View Team Tasks
 * <p>
 * This test verifies the implementation of the team tasks view according to the use case
 * specification. It covers all main success scenarios and alternative flows defined in
 * UC-006.
 */
class TaskViewTest extends AbstractBrowserlessTest {

	/**
	 * UC-006 Main Success Scenario: User with single team Steps 1-3: User navigates to
	 * view, system determines teams, auto-selects single team
	 * <p>
	 * BR-001: Team Membership Required BR-002: All Team Tasks Visible BR-003: Task List
	 * Ordering (sorted by updated_at DESC)
	 */
	@Test
	@WithMockUser(username = "singleTeamUser", roles = "USER")
	void user_with_single_team_sees_tasks() {
		navigate(TaskView.class);

		@SuppressWarnings("unchecked")
		ComboBox<Team> teamSelector = $(ComboBox.class).withCaption("Team").single();
		assertSoftly(softly -> {
			softly.assertThat(teamSelector.getValue()).isNotNull();
			softly.assertThat(teamSelector.getValue().name()).isEqualTo("Development Team");
			softly.assertThat(teamSelector.isReadOnly()).isTrue();
		});

		@SuppressWarnings("unchecked")
		Grid<Task> grid = $(Grid.class).single();
		assertThat(test(grid).size()).isEqualTo(3);

		assertThat(grid.getColumns()).hasSize(6);

		// BR-003: Tasks are ordered by updated_at DESC
		var task1 = test(grid).getRow(0);
		var task2 = test(grid).getRow(1);
		var task3 = test(grid).getRow(2);

		assertThat(task1.title()).isEqualTo("Fix navigation bug");
		assertThat(task2.title()).isEqualTo("Implement login feature");
		assertThat(task3.title()).isEqualTo("Write documentation");

		assertSoftly(softly -> {
			softly.assertThat(task1.status()).isEqualTo("ASSIGNED");
			softly.assertThat(task1.assignedTo()).isEqualTo("admin");
			softly.assertThat(task1.createdBy()).isEqualTo("user");
		});

		assertThat(task3.getAssignedToDisplay()).isEqualTo("Unassigned");
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void user_with_multiple_teams_can_select_team() {
		navigate(TaskView.class);

		@SuppressWarnings("unchecked")
		ComboBox<Team> teamSelector = $(ComboBox.class).withCaption("Team").single();
		assertThat(teamSelector.isReadOnly()).isFalse();

		assertThat(teamSelector.getValue()).isNotNull();
		var initialTeam = teamSelector.getValue();
		assertThat(initialTeam.name()).isIn("Development Team", "QA Team");

		@SuppressWarnings("unchecked")
		Grid<Task> grid = $(Grid.class).single();
		var initialSize = test(grid).size();
		assertThat(initialSize).isGreaterThan(0);

		var availableTeams = List.of(
				new Team(1L, "Development Team", "Main development team", LocalDateTime.now(), true),
				new Team(2L, "QA Team", "Quality assurance team", LocalDateTime.now(), true));
		var otherTeam = availableTeams.stream()
			.filter(t -> !t.name().equals(initialTeam.name()))
			.findFirst()
			.orElseThrow();

		teamSelector.setValue(otherTeam);

		var newSize = test(grid).size();
		assertThat(newSize).isGreaterThan(0);

		for (int i = 0; i < newSize; i++) {
			assertThat(test(grid).getRow(i).teamId()).isEqualTo(otherTeam.id());
		}
	}

	/**
	 * UC-006 Alternative Flow A1: No Tasks in Team
	 */
	@Test
	@WithMockUser(username = "admin", roles = "USER")
	void displays_empty_state_when_team_has_no_tasks() {
		navigate(TaskView.class);

		@SuppressWarnings("unchecked")
		ComboBox<Team> teamSelector = $(ComboBox.class).withCaption("Team").single();

		var emptyTeam = new Team(3L, "Empty Team", "Team with no tasks", LocalDateTime.now(), true);
		teamSelector.setValue(emptyTeam);

		var emptyStateMessage = $(Span.class).single();
		assertSoftly(softly -> {
			softly.assertThat(emptyStateMessage.isVisible()).isTrue();
			softly.assertThat(emptyStateMessage.getText()).isEqualTo("No tasks found for this team.");
		});

		var taskView = $(TaskView.class).single();
		var layout = (VerticalLayout) taskView.getChildren().findFirst().orElseThrow();
		Grid<?> grid = (Grid<?>) layout.getChildren()
			.filter(Grid.class::isInstance)
			.findFirst()
			.orElseThrow(() -> new AssertionError("Grid not found"));
		assertThat(grid.isVisible()).isFalse();
	}

	/**
	 * UC-006 Alternative Flow A2: User Not Member of Any Team
	 */
	@Test
	@WithMockUser(username = "noTeam", roles = "USER")
	void displays_message_when_user_not_member_of_any_team() {
		navigate(TaskView.class);

		var emptyStateMessage = $(Span.class).single();
		assertSoftly(softly -> {
			softly.assertThat(emptyStateMessage.isVisible()).isTrue();
			softly.assertThat(emptyStateMessage.getText())
				.isEqualTo("You are not a member of any team. Contact your administrator.");
		});

		var taskView = $(TaskView.class).single();
		var layout = (VerticalLayout) taskView.getChildren().findFirst().orElseThrow();
		ComboBox<?> teamSelector = (ComboBox<?>) layout.getChildren()
			.filter(ComboBox.class::isInstance)
			.findFirst()
			.orElseThrow(() -> new AssertionError("ComboBox not found"));
		assertThat(teamSelector.isVisible()).isFalse();

		Grid<?> grid = (Grid<?>) layout.getChildren()
			.filter(Grid.class::isInstance)
			.findFirst()
			.orElseThrow(() -> new AssertionError("Grid not found"));
		assertThat(grid.isVisible()).isFalse();
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void displays_all_required_task_columns() {
		navigate(TaskView.class);

		@SuppressWarnings("unchecked")
		Grid<Task> grid = $(Grid.class).single();

		var columns = grid.getColumns();
		assertThat(columns).hasSizeGreaterThanOrEqualTo(5);

		assertThat(test(grid).size()).isGreaterThan(0);
	}

	/**
	 * UC-006 Business Rule BR-002: All Team Tasks Visible
	 */
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void user_sees_all_team_tasks_not_just_assigned_tasks() {
		navigate(TaskView.class);

		@SuppressWarnings("unchecked")
		Grid<Task> grid = $(Grid.class).single();
		var size = test(grid).size();

		long assignedToOthers = 0;
		long unassigned = 0;
		long assignedToSelf = 0;
		for (int i = 0; i < size; i++) {
			var task = test(grid).getRow(i);
			if ("admin".equals(task.assignedTo())) {
				assignedToOthers++;
			}
			else if (task.assignedTo() == null) {
				unassigned++;
			}
			else if ("user".equals(task.assignedTo())) {
				assignedToSelf++;
			}
		}

		assertThat(assignedToOthers + unassigned + assignedToSelf).isEqualTo(size);
		assertThat(size).isGreaterThanOrEqualTo(3);
	}

	/**
	 * UC-006 Postcondition: Task list displays current information from database
	 */
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void displays_current_task_information_from_database() {
		navigate(TaskView.class);

		@SuppressWarnings("unchecked")
		Grid<Task> grid = $(Grid.class).single();
		var size = test(grid).size();

		assertThat(size).isGreaterThan(0);

		for (int i = 0; i < size; i++) {
			var task = test(grid).getRow(i);
			assertSoftly(softly -> {
				softly.assertThat(task.id()).isNotNull();
				softly.assertThat(task.teamId()).isNotNull();
				softly.assertThat(task.title()).isNotBlank();
				softly.assertThat(task.status()).isNotBlank();
				softly.assertThat(task.createdBy()).isNotBlank();
				softly.assertThat(task.createdAt()).isNotNull();
				softly.assertThat(task.updatedAt()).isNotNull();
			});
		}
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void displays_correct_page_title() {
		var taskView = navigate(TaskView.class);

		assertThat(taskView.getPageTitle()).isEqualTo("Team Tasks");
	}

}
