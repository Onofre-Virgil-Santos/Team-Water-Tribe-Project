package com.watertribe.todo.cucumber.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class TodoPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private static final By MAIN_TODO_INPUT     = By.cssSelector("input#task");
    private static final By MAIN_TODO_SUBMIT    = By.cssSelector("button[type='submit']");
    private static final By EXPAND_BTN          = By.cssSelector(".btn-expand");
    private static final By SUBTASK_PANEL       = By.cssSelector(".subtodo-panel");
    private static final By SUBTASK_INPUT       = By.cssSelector(".subtodo-input");
    private static final By SUBTASK_ADD_BTN     = By.cssSelector(".btn-subtodo-add");
    private static final By SUBTASK_EMPTY       = By.cssSelector(".subtodo-empty");
    private static final By SUBTASK_EDIT_INPUT  = By.cssSelector(".subtodo-edit-input");
    private static final By SUBTASK_SAVE_BTN    = By.cssSelector(".btn-subtodo-save");

    public TodoPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open() {
        driver.get("http://localhost:4200/todo");
    }

    public void createMainTodo(String task) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(MAIN_TODO_INPUT));
        input.clear();
        input.sendKeys(task);
        driver.findElement(MAIN_TODO_SUBMIT).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//span[contains(@class,'todo-task') and text()='" + task + "']")
        ));
    }

    public void expandTodo(String task) {
        WebElement todoRow = getTodoRowByTask(task);
        todoRow.findElement(EXPAND_BTN).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(SUBTASK_PANEL));
    }

    public void typeInSubtaskInput(String text) {
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(SUBTASK_INPUT));
        input.clear();
        input.sendKeys(text);
    }

    public void clickAddSubtaskButton() {
        driver.findElement(SUBTASK_ADD_BTN).click();
    }

    public void clickEditButtonForSubtask(String task) {
        WebElement item = getSubtaskItemByText(task);
        item.findElement(By.cssSelector(".btn-edit")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(SUBTASK_EDIT_INPUT));
    }

    public void clearEditInputAndType(String text) {
        WebElement editInput = wait.until(ExpectedConditions.elementToBeClickable(SUBTASK_EDIT_INPUT));
        editInput.clear();
        editInput.sendKeys(text);
    }

    public void clickSaveSubtaskButton() {
        driver.findElement(SUBTASK_SAVE_BTN).click();
    }

    public void deleteSubtask(String task) {
        WebElement item = getSubtaskItemByText(task);
        item.findElement(By.cssSelector(".btn-delete")).click();
    }

    public void checkSubtaskCheckbox(String task) {
        WebElement item = getSubtaskItemByText(task);
        item.findElement(By.cssSelector(".subtodo-checkbox")).click();
    }

    public boolean isSubtaskPanelVisible() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(SUBTASK_PANEL)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isSubtaskInList(String task) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//span[contains(@class,'subtodo-task') and text()='" + task + "']")
            ));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isSubtaskAbsent(String task) {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.xpath("//span[contains(@class,'subtodo-task') and text()='" + task + "']")
            ));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean isSubtaskCompleted(String task) {
        WebElement span = wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//span[contains(@class,'subtodo-task') and text()='" + task + "']")
        ));
        return span.getAttribute("class").contains("strikethrough");
    }

    public boolean isEmptySubtaskMessageDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(SUBTASK_EMPTY)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    private WebElement getTodoRowByTask(String task) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[contains(@class,'todo-item') and .//span[contains(@class,'todo-task') and text()='" + task + "']]")
        ));
    }

    private WebElement getSubtaskItemByText(String task) {
        return wait.until(ExpectedConditions.presenceOfElementLocated(
            By.xpath("//div[contains(@class,'subtodo-item') and .//span[contains(@class,'subtodo-task') and text()='" + task + "']]")
        ));
    }
}
