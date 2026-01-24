package ch.martinelli.demo.aiup.task.ui;

import ch.martinelli.demo.aiup.core.domain.Role;
import ch.martinelli.demo.aiup.core.security.SecurityContext;
import ch.martinelli.demo.aiup.core.ui.preferences.UserPreferencesService;
import ch.martinelli.demo.aiup.task.domain.Task;
import ch.martinelli.demo.aiup.task.domain.TaskDAO;
import ch.martinelli.demo.aiup.team.domain.Team;
import ch.martinelli.demo.aiup.team.domain.TeamDAO;
import ch.martinelli.oss.vaadinjooq.util.VaadinJooqUtil;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridSortOrder;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;

import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static ch.martinelli.demo.aiup.db.tables.Task.TASK;

@RolesAllowed({ Role.USER, Role.ADMIN })
@Route(value = "")
public class TaskView extends Div implements HasDynamicTitle {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	private final transient SecurityContext securityContext;

	private final transient TeamDAO teamDAO;

	private final transient TaskDAO taskDAO;

	private final transient UserPreferencesService userPreferencesService;

	private final Grid<Task> grid = new Grid<>();

	private final ComboBox<Team> teamSelector = new ComboBox<>(getTranslation("task.team"));

	private final Span emptyStateMessage = new Span();

	public TaskView(SecurityContext securityContext, TeamDAO teamDAO, TaskDAO taskDAO,
			UserPreferencesService userPreferencesService) {
		this.securityContext = securityContext;
		this.teamDAO = teamDAO;
		this.taskDAO = taskDAO;
		this.userPreferencesService = userPreferencesService;

		setSizeFull();

		var layout = createLayout();
		add(layout);

		initializeView();
	}

	@Override
	public String getPageTitle() {
		return getTranslation("task.title");
	}

	private VerticalLayout createLayout() {
		emptyStateMessage.setVisible(false);
		emptyStateMessage.getStyle().set("color", "var(--lumo-secondary-text-color)");
		emptyStateMessage.getStyle().set("font-style", "italic");

		grid.setSizeFull();
		grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

		grid.addColumn(Task::title)
			.setHeader(getTranslation("task.taskTitle"))
			.setSortable(true)
			.setSortProperty(TASK.TITLE.getName())
			.setAutoWidth(true);

		grid.addColumn(Task::status)
			.setHeader(getTranslation("task.status"))
			.setSortable(true)
			.setSortProperty(TASK.STATUS.getName())
			.setAutoWidth(true);

		grid.addColumn(Task::getAssignedToDisplay).setHeader(getTranslation("task.assignedTo")).setAutoWidth(true);

		grid.addColumn(task -> task.createdAt() != null ? DATE_FORMATTER.format(task.createdAt()) : "")
			.setHeader(getTranslation("task.createdDate"))
			.setSortable(true)
			.setSortProperty(TASK.CREATED_AT.getName())
			.setAutoWidth(true);

		grid.addColumn(Task::createdBy).setHeader(getTranslation("task.createdBy")).setAutoWidth(true);

		var updatedAtColumn = grid.addColumn(Task::updatedAt);
		updatedAtColumn.setHeader(getTranslation("task.updatedAt"));
		updatedAtColumn.setSortable(true);
		updatedAtColumn.setSortProperty(TASK.UPDATED_AT.getName());
		updatedAtColumn.setVisible(false);

		grid.sort(GridSortOrder.desc(updatedAtColumn).build());

		teamSelector.setItemLabelGenerator(Team::name);
		teamSelector.addValueChangeListener(event -> {
			var team = event.getValue();
			if (team != null) {
				userPreferencesService.setLastSelectedTeamId(team.id());
				refreshGrid();
			}
		});
		teamSelector.setWidthFull();

		var verticalLayout = new VerticalLayout(teamSelector, emptyStateMessage, grid);
		verticalLayout.setSizeFull();
		verticalLayout.setSpacing(true);
		verticalLayout.setPadding(true);

		return verticalLayout;
	}

	private void initializeView() {
		var currentUser = securityContext.getLoggedInUser();
		if (currentUser.isEmpty()) {
			return;
		}

		var username = currentUser.get().getUsername();
		var userTeams = teamDAO.findTeamsByUsername(username);

		if (userTeams.isEmpty()) {
			teamSelector.setVisible(false);
			grid.setVisible(false);
			emptyStateMessage.setText(getTranslation("task.notMemberOfTeam"));
			emptyStateMessage.setVisible(true);
			return;
		}

		teamSelector.setItems(userTeams);

		if (userTeams.size() == 1) {
			teamSelector.setValue(userTeams.getFirst());
			teamSelector.setReadOnly(true);
		}
		else {
			var lastSelectedTeamId = userPreferencesService.getLastSelectedTeamId().orElse(null);
			var lastSelectedTeam = userTeams.stream()
				.filter(team -> team.id().equals(lastSelectedTeamId))
				.findFirst()
				.orElse(userTeams.getFirst());
			teamSelector.setValue(lastSelectedTeam);
		}

		setupGridDataProvider();
	}

	private void setupGridDataProvider() {
		grid.setItems(query -> {
			var selectedTeam = teamSelector.getValue();
			if (selectedTeam == null) {
				return Stream.empty();
			}

			var tasks = taskDAO.findTasksByTeamId(selectedTeam.id(), query.getOffset(), query.getLimit(),
					VaadinJooqUtil.orderFields(TASK, query));

			if (tasks.isEmpty()) {
				emptyStateMessage.setText(getTranslation("task.noTasksFound"));
				emptyStateMessage.setVisible(true);
				grid.setVisible(false);
			}
			else {
				emptyStateMessage.setVisible(false);
				grid.setVisible(true);
			}

			return tasks.stream();
		});
	}

	private void refreshGrid() {
		emptyStateMessage.setVisible(false);
		grid.setVisible(true);
		grid.getDataProvider().refreshAll();
	}

}
