package com.suriya.todo.testcases;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.suriya.todo.base.TestBase;
import com.suriya.todo.pages.ToDoPage;

public class ToDoTest extends TestBase {

	ToDoPage toDoPage;

	// Constructor
	public ToDoTest() {
		super();
	}

	// Initialize WebDriver and page object before each test method
	@BeforeMethod
	public void setUp() {
		initialization();
		toDoPage = new ToDoPage();
	}

	@Test(priority = 1, enabled=true)
	public void testAddToDo() {
		
		toDoPage.addToDo();
		
		  Assert.assertTrue(toDoPage.isToDoPresent("Drink Water Every Hour"));
	      Assert.assertTrue(toDoPage.isToDoPresent("Exercise Daily"));
	}
	
	@Test(priority = 2, enabled=true)
    public void testMarkCompleted() throws InterruptedException {
		toDoPage.addToDo();
		Thread.sleep(3000);
        toDoPage.markCompleted("Drink Water Every Hour");
        toDoPage.markCompleted("Exercise Daily");
        Thread.sleep(3000);
        Assert.assertTrue(toDoPage.isCompleted("Drink Water Every Hour"));
    }

    @Test(priority = 3, enabled=true)
    public void testDeleteToDo() throws InterruptedException {
    	toDoPage.addToDo();
		Thread.sleep(3000);
        toDoPage.deleteToDo("Drink Water Every Hour");
        Thread.sleep(3000);
        Assert.assertFalse(toDoPage.isToDoPresent("Drink Water Every Hour"));
    }

    @Test(priority = 4, enabled=true)
    public void testFilterToDos() throws InterruptedException {
    	
    	toDoPage.addToDo();
		Thread.sleep(3000);
        toDoPage.filterActive();
        Assert.assertTrue(toDoPage.isToDoPresent("Drink Water Every Hour"));
	    Assert.assertTrue(toDoPage.isToDoPresent("Exercise Daily"));
        toDoPage.filterCompleted();
        Assert.assertTrue(toDoPage.isToDoPresent("Drink Water Every Hour"));
	    Assert.assertTrue(toDoPage.isToDoPresent("Exercise Daily"));

    }
	
	@AfterMethod(alwaysRun = true)
	public void tearDown() {
	    TestBase.quitDriver();
	}
}
