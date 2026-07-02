package com.watertribe.todo.cucumber;

import com.watertribe.todo.cucumber.pages.LoginPage;
import com.watertribe.todo.cucumber.pages.TodoPage;
import com.watertribe.todo.repository.MainTodoRepository;
import com.watertribe.todo.repository.SubTaskRepository;
import com.watertribe.todo.repository.UserRepository;
import com.watertribe.todo.service.UserService;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SubTaskSteps {

    @Autowired UserService userService;
    @Autowired UserRepository userRepository;
    @Autowired MainTodoRepository mainTodoRepository;
    @Autowired SubTaskRepository subTaskRepository;

    private WebDriver driver;
    private LoginPage loginPage;
    private TodoPage todoPage;

    private static final String USERNAME = "e2euser";
    private static final String EMAIL    = "e2e@example.com";
    private static final String PASSWORD = "password123";

    @Before
    public void setUp() {
        subTaskRepository.deleteAll();
        mainTodoRepository.deleteAll();
        userRepository.deleteAll();

        driver    = DriverManager.getDriver();
        loginPage = new LoginPage(driver);
        todoPage  = new TodoPage(driver);
    }

    @After
    public void tearDown() {
        String url = driver.getCurrentUrl();
        if (url != null && !url.startsWith("data:")) {
            driver.manage().deleteAllCookies();
            ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.clear(); window.sessionStorage.clear();"
            );
        }
    }

    @AfterAll
    public static void closeBrowser() {
        DriverManager.closeDriver();
    }

    @Given("the user is registered and logged in")
    public void theUserIsRegisteredAndLoggedIn() {
        userService.register(USERNAME, EMAIL, PASSWORD);
        loginPage.open();
        loginPage.login(USERNAME, PASSWORD);
        todoPage.open();
    }

    @Given("a main todo {string} exists")
    public void aMainTodoExists(String task) {
        todoPage.createMainTodo(task);
    }

    @Given("the subtask {string} exists under {string}")
    public void theSubtaskExistsUnder(String subtask, String mainTask) {
        todoPage.expandTodo(mainTask);
        todoPage.typeInSubtaskInput(subtask);
        todoPage.clickAddSubtaskButton();
        assertTrue(todoPage.isSubtaskInList(subtask),
            "Seed failed: subtask '" + subtask + "' did not appear after creation");
    }

    @When("the user expands the main todo {string}")
    public void theUserExpandsMainTodo(String task) {
        todoPage.expandTodo(task);
    }

    @And("the user types {string} into the subtask input")
    public void theUserTypesIntoSubtaskInput(String text) {
        todoPage.typeInSubtaskInput(text);
    }

    @And("the user clicks the add subtask button")
    public void theUserClicksAddSubtaskButton() {
        todoPage.clickAddSubtaskButton();
    }

    @And("the user clicks the edit button for subtask {string}")
    public void theUserClicksEditButtonForSubtask(String task) {
        todoPage.clickEditButtonForSubtask(task);
    }

    @And("the user clears the subtask edit input and types {string}")
    public void theUserClearsEditInputAndTypes(String text) {
        todoPage.clearEditInputAndType(text);
    }

    @And("the user clicks the save subtask button")
    public void theUserClicksSaveSubtaskButton() {
        todoPage.clickSaveSubtaskButton();
    }

    @And("the user deletes the subtask {string}")
    public void theUserDeletesSubtask(String task) {
        todoPage.deleteSubtask(task);
    }

    @And("the user checks the checkbox for subtask {string}")
    public void theUserChecksCheckboxForSubtask(String task) {
        todoPage.checkSubtaskCheckbox(task);
    }

    @Then("the subtask panel is visible")
    public void theSubtaskPanelIsVisible() {
        assertTrue(todoPage.isSubtaskPanelVisible(),
            "Expected subtask panel to be visible");
    }

    @Then("the subtask {string} appears in the list")
    public void theSubtaskAppearsInList(String task) {
        assertTrue(todoPage.isSubtaskInList(task),
            "Expected subtask '" + task + "' to appear in the list");
    }

    @Then("the subtask {string} is no longer in the list")
    public void theSubtaskIsNoLongerInList(String task) {
        assertTrue(todoPage.isSubtaskAbsent(task),
            "Expected subtask '" + task + "' to be gone from the list");
    }

    @Then("the subtask {string} is shown as completed")
    public void theSubtaskIsShownAsCompleted(String task) {
        assertTrue(todoPage.isSubtaskCompleted(task),
            "Expected subtask '" + task + "' to have strikethrough styling");
    }

    @Then("the empty subtask message is displayed")
    public void theEmptySubtaskMessageIsDisplayed() {
        assertTrue(todoPage.isEmptySubtaskMessageDisplayed(),
            "Expected 'No subtasks yet.' message to be visible");
    }
}
