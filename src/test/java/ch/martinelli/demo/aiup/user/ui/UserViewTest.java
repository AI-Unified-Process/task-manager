package ch.martinelli.demo.aiup.user.ui;

import ch.martinelli.demo.aiup.core.domain.Role;
import ch.martinelli.demo.aiup.core.domain.UserWithRoles;
import ch.martinelli.demo.aiup.core.ui.AbstractBrowserlessTest;
import ch.martinelli.demo.aiup.core.ui.UserView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@WithMockUser(username = "admin", roles = "ADMIN")
class UserViewTest extends AbstractBrowserlessTest {

	@BeforeEach
	void openView() {
		navigate(UserView.class);
	}

	@Test
	void check_grid_size() {
		@SuppressWarnings("unchecked")
		Grid<UserWithRoles> grid = find(Grid.class).single();
		assertThat(test(grid).size()).isEqualTo(2);
	}

	@Test
	void navigate_to_user() {
		navigate(UserView.class, "admin");

		@SuppressWarnings("unchecked")
		Grid<UserWithRoles> grid = find(Grid.class).single();
		assertThat(test(grid).size()).isEqualTo(2);

		Set<UserWithRoles> selectedItems = grid.getSelectedItems();
		assertThat(selectedItems).hasSize(1)
			.first()
			.extracting(userWithRoles -> userWithRoles.getUser().getFirstName())
			.isEqualTo("Emma");

		var firstNameTextField = find(TextField.class).withCaption("First Name").single();
		assertThat(firstNameTextField.getValue()).isEqualTo("Emma");
	}

	@Test
	void delete_person() {
		@SuppressWarnings("unchecked")
		Grid<UserWithRoles> grid = find(Grid.class).single();
		assertThat(test(grid).size()).isEqualTo(2);

		var component = test(grid).getCellComponent(0, "actions");
		assertThat(component).isInstanceOf(SvgIcon.class);
		ComponentUtil.fireEvent(component, new ClickEvent<>(component));

		var confirmDialog = find(ConfirmDialog.class).single();
		test(confirmDialog).confirm();

		assertThat(test(grid).size()).isEqualTo(1);
	}

	@Test
	void save_new_user() {
		@SuppressWarnings("unchecked")
		Grid<UserWithRoles> grid = find(Grid.class).single();
		var initialSize = test(grid).size();

		clickAddIcon(grid);

		var usernameField = find(TextField.class).withCaption("Username").single();
		var firstNameField = find(TextField.class).withCaption("First Name").single();
		var lastNameField = find(TextField.class).withCaption("Last Name").single();
		var passwordField = find(PasswordField.class).withCaption("Password").single();
		@SuppressWarnings({ "unchecked", "rawtypes" })
		MultiSelectComboBox roleMultiSelect = find(MultiSelectComboBox.class).withCaption("Roles").single();

		usernameField.setValue("testuser");
		firstNameField.setValue("Test");
		lastNameField.setValue("User");
		passwordField.setValue("password123");
		roleMultiSelect.setValue(Set.of(Role.USER));

		var saveButton = find(Button.class).withCaption("Save").single();
		test(saveButton).click();

		assertThat(test(grid).size()).isEqualTo(initialSize + 1);
	}

	@Test
	void save_existing_user() {
		navigate(UserView.class, "user");

		var firstNameField = find(TextField.class).withCaption("First Name").single();
		var passwordField = find(PasswordField.class).withCaption("Password").single();
		test(passwordField).setValue("password");

		var updatedFirstName = "UpdatedJohn";
		firstNameField.setValue(updatedFirstName);

		var saveButton = find(Button.class).withCaption("Save").single();
		test(saveButton).click();

		navigate(UserView.class, "user");
		var updatedFirstNameField = find(TextField.class).withCaption("First Name").single();
		assertThat(updatedFirstNameField.getValue()).isEqualTo(updatedFirstName);
	}

	@Test
	void save_validation_fails_for_empty_required_fields() {
		@SuppressWarnings("unchecked")
		Grid<UserWithRoles> grid = find(Grid.class).single();
		clickAddIcon(grid);

		var saveButton = find(Button.class).withCaption("Save").single();
		test(saveButton).click();

		var usernameField = find(TextField.class).withCaption("Username").single();
		var firstNameField = find(TextField.class).withCaption("First Name").single();
		var lastNameField = find(TextField.class).withCaption("Last Name").single();
		var passwordField = find(PasswordField.class).withCaption("Password").single();

		assertThat(usernameField.isInvalid()).isTrue();
		assertThat(firstNameField.isInvalid()).isTrue();
		assertThat(lastNameField.isInvalid()).isTrue();
		assertThat(passwordField.isInvalid()).isTrue();
	}

	@Test
	void cancel_button_clears_form_and_refreshes_grid() {
		@SuppressWarnings("unchecked")
		Grid<UserWithRoles> grid = find(Grid.class).single();
		clickAddIcon(grid);

		var usernameField = find(TextField.class).withCaption("Username").single();
		var firstNameField = find(TextField.class).withCaption("First Name").single();

		usernameField.setValue("testuser");
		firstNameField.setValue("Test");

		var cancelButton = find(Button.class).withCaption("Cancel").single();
		test(cancelButton).click();

		assertThat(usernameField.getValue()).isEmpty();
		assertThat(firstNameField.getValue()).isEmpty();
		assertThat(usernameField.isReadOnly()).isFalse();
	}

	private void clickAddIcon(Grid<UserWithRoles> grid) {
		var icon = (SvgIcon) grid.getColumnByKey("actions").getHeaderComponent();
		ComponentUtil.fireEvent(icon, new ClickEvent<>(icon));
	}

}
