/*
 * Copyright (c) 2024 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.test.common;

import io.github.bonigarcia.wdm.WebDriverManager;

import java.time.Duration;

import java.util.List;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import org.openqa.selenium.support.ui.WebDriverWait;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class BaseTodoListTests
    {
    // ----- test lifecycle -------------------------------------------------

    @BeforeAll
    static void setup()
        {
        String proxyHost = System.getProperty("http.proxyHost");
        String proxyPort = System.getProperty("http.proxyPort");
        
        if (proxyHost != null)
            {
            proxyHost = proxyHost.trim();
            if (proxyHost.isEmpty())
                {
                proxyHost = null;
                }
            }

        if (proxyPort != null)
            {
            proxyPort = proxyPort.trim();
            if (proxyPort.isEmpty())
                {
                proxyPort = null;
                }
            }

        if (proxyHost != null && proxyPort != null)
            {
            WebDriverManager.firefoxdriver().clearDriverCache().useMirror().proxy(proxyHost + ':' + proxyPort).setup();
            }
        else
            {
            WebDriverManager.firefoxdriver().clearDriverCache().useMirror().setup();
            }
        }

    @BeforeEach
    void initDriver()
        {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        driver = new FirefoxDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5L));
        }

    @AfterEach
    void tearDown()
        {
        if (driver != null)
            {
            driver.quit();
            }
        }

    // ----- test methods ---------------------------------------------------

    @Test
    public void validateAddCompleteAndRemoveTask()
        {
        driver.get(getUrl());

        assertEquals("Coherence To Do Example Application", driver.getTitle());

        List<WebElement> elements = driver.findElements(By.tagName("li"));
        assertTrue(elements.isEmpty());

        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        Optional<WebElement> inputOpt =
                inputs.stream()
                        .filter(webElement -> "new-todo".equals(webElement.getAttribute("class")))
                        .findFirst();

        assertTrue(inputOpt.isPresent(), "Unable to locating input with class 'new-todo'");

        WebElement tasksParent = driver.findElement(By.className("todo-list"));
        WebElement input       = inputOpt.get();
        input.sendKeys("New Task");
        input.sendKeys(Keys.ENTER);
        new WebDriverWait(driver, Duration.ofSeconds(5L))
                .until(webDriver -> tasksParent.findElements(By.tagName("li")).size() == 1);

        List<WebElement> listTasks = tasksParent.findElements(By.tagName("li"));
        assertFalse(listTasks.isEmpty());

        WebElement task = listTasks.get(0);
        WebElement label = task.findElement(By.tagName("label"));
        assertNotNull(label);
        assertEquals("New Task", label.getText());

        // get parent li element and ensure it's not marked as completed
        WebElement ul = label.findElement(By.xpath("../.."));
        assertNotNull(ul);
        assertEquals("", ul.getAttribute("class"));

        // get parent div and get the completion toggle
        WebElement div = label.findElement(By.xpath(".."));
        assertNotNull(div);

        WebElement complete = div.findElement(By.tagName("input"));
        assertNotNull(complete);
        assertEquals("toggle", complete.getAttribute("class"));

        // mark the task as completed
        complete.click();

        // validate the complete class is applied to the parent ul
        assertEquals("completed", ul.getAttribute("class"));

        // delete the completed task directly
        WebElement remove = div.findElement(By.tagName("button"));
        assertNotNull(remove);

        // click the remove button
        remove.click();

        // validate no tasks are presently rendered
        new WebDriverWait(driver, Duration.ofSeconds(5L))
                .until(webDriver -> tasksParent.findElements(By.tagName("li")).isEmpty());
        }

    @Test
    public void validateTasksAllActiveCompletedActions()
        {
        driver.get(getUrl());

        List<WebElement> inputs = driver.findElements(By.tagName("input"));
        Optional<WebElement> inputOpt =
                inputs.stream()
                        .filter(webElement -> "new-todo".equals(webElement.getAttribute("class")))
                        .findFirst();

        assertTrue(inputOpt.isPresent(), "Unable to locating input with class 'new-todo'");

        WebElement tasksParent = driver.findElement(By.className("todo-list"));
        WebElement input       = inputOpt.get();
        input.sendKeys("Task1");
        input.sendKeys(Keys.ENTER);
        new WebDriverWait(driver, Duration.ofSeconds(5L))
                .until(webDriver -> tasksParent.findElements(By.tagName("li")).size() == 1);

        input.sendKeys("Task2");
        input.sendKeys(Keys.ENTER);
        new WebDriverWait(driver, Duration.ofSeconds(5L))
                .until(webDriver -> tasksParent.findElements(By.tagName("li")).size() == 2);

        input.sendKeys("Task3");
        input.sendKeys(Keys.ENTER);
        new WebDriverWait(driver, Duration.ofSeconds(5L))
                .until(webDriver -> tasksParent.findElements(By.tagName("li")).size() == 3);

        List<WebElement> listTasks = tasksParent.findElements(By.tagName("li"));
        assertEquals(3, listTasks.size());
        assertEquals(3, listTasks.stream()
                .filter(webElement -> "".equals(webElement.getAttribute("class")))
                .count());
        assertEquals("Task1", listTasks.get(0).getText());
        assertEquals("Task2", listTasks.get(1).getText());
        assertEquals("Task3", listTasks.get(2).getText());

        // get the first task's completion toggle
        WebElement complete = tasksParent.findElement(By.tagName("input"));
        assertNotNull(complete);
        assertEquals("toggle", complete.getAttribute("class"));

        // mark the task as completed
        complete.click();

        // get reference to the button container
        WebElement ulFilters = driver.findElement(By.className("filters"));
        assertNotNull(ulFilters);

        // get the buttons
        List<WebElement> listButtons = ulFilters.findElements(By.tagName("button"));
        assertEquals(3, listButtons.size());

        WebElement buttonAll       = listButtons.get(0);
        WebElement buttonActive    = listButtons.get(1);
        WebElement buttonCompleted = listButtons.get(2);

        // click the active button and verify visible tasks
        buttonActive.click();
        new WebDriverWait(driver, Duration.ofSeconds(5L))
                .until(webDriver -> tasksParent.findElements(By.tagName("li")).size() == 2);
        listTasks = tasksParent.findElements(By.tagName("li"));
        assertEquals(2, listTasks.size());
        assertEquals(2, listTasks.stream()
                .filter(webElement -> "".equals(webElement.getAttribute("class")))
                .count());
        assertEquals("Task2", listTasks.get(0).getText());
        assertEquals("Task3", listTasks.get(1).getText());

        // click the completed button and verify tasks
        buttonCompleted.click();
        new WebDriverWait(driver, Duration.ofSeconds(5L))
                .until(webDriver -> tasksParent.findElements(By.tagName("li")).size() == 1);
        listTasks = tasksParent.findElements(By.tagName("li"));
        assertEquals(1, listTasks.size());
        assertEquals(1, listTasks.stream()
                .filter(webElement -> "completed".equals(webElement.getAttribute("class")))
                .count());
        assertEquals("Task1", listTasks.get(0).getText());

        // click the all button and verify visible tasks
        buttonAll.click();
        new WebDriverWait(driver, Duration.ofSeconds(5L))
                .until(webDriver -> tasksParent.findElements(By.tagName("li")).size() == 3);
        listTasks = tasksParent.findElements(By.tagName("li"));
        assertEquals(3, listTasks.size());
        assertEquals("Task1", listTasks.get(0).getText());
        assertEquals("Task2", listTasks.get(1).getText());
        assertEquals("Task3", listTasks.get(2).getText());

        // click the clear completed button and verify visible tasks
        WebElement buttonClearCompleted = driver.findElement(By.className("clear-completed"));
        assertNotNull(buttonClearCompleted);
        buttonClearCompleted.click();
        new WebDriverWait(driver, Duration.ofSeconds(5L))
                .until(webDriver -> tasksParent.findElements(By.tagName("li")).size() == 2);
        listTasks = tasksParent.findElements(By.tagName("li"));
        assertEquals(2, listTasks.size());
        assertEquals("Task2", listTasks.get(0).getText());
        assertEquals("Task3", listTasks.get(1).getText());
        }

    // ----- helper methods -------------------------------------------------

    protected abstract String getUrl();

    // ----- data members ---------------------------------------------------

    protected WebDriver driver;
    }
