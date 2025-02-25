package com.suriya.todo.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.suriya.todo.base.TestBase;

public class ToDoPage extends TestBase {

	// Page Factory

	@FindBy(className = "new-todo")
	WebElement todoInput;

	@FindBy(css = ".todo-list li")
	List<WebElement> todoList; 

	@FindBy(className = "todo-count")
	WebElement todoCount;

	// Initializing the Page Objects:
	public ToDoPage() {

		PageFactory.initElements(driver, this);
	}

	public boolean isToDoPresent(String task) {
		for (WebElement item : todoList) {
			if (item.getText().contains(task)) {
				return true;
			}
		}
		return false;
	}

	// User actions

	// Add multiple to-do tasks
	public void addToDo() {
		addTask("Drink Water Every Hour");
		addTask("Exercise Daily");
		addTask("Clean the House");
		addTask("Meditate Daily");
	}

	// Helper method to add a task
	private void addTask(String task) {
		todoInput.sendKeys(task);
		todoInput.sendKeys(Keys.ENTER);
	}

	public void markCompleted(String task) {
		for (WebElement item : todoList) {
			if (item.getText().contains(task)) {
				item.findElement(By.cssSelector("input[class='toggle']")).click(); // Toggle the checkbox to mark as completed
				break;
			}
		}
	}

	public boolean isCompleted(String task) {
		for (WebElement item : todoList) {
			if (item.getText().contains(task) && item.getAttribute("class").contains("completed")) {
				return true;
			}
		}
		return false;
	}

	public void deleteToDo(String task) {
		for (WebElement item : todoList) {
			if (item.getText().contains(task)) {
				item.findElement(By.cssSelector("button.destroy")).click(); // Click the destroy button (delete)
				break;
			}
		}
	}

	public void filterActive() {
		driver.findElement(By.linkText("Active")).click();
	}

	public void filterCompleted() {
		driver.findElement(By.linkText("Completed")).click();
	}

}
