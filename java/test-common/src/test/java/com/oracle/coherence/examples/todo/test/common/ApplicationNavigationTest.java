/*
 * Copyright (c) 2026 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.test.common;

import com.sun.net.httpserver.HttpServer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApplicationNavigationTest
    {
    @BeforeAll
    static void setupDriver()
        {
        BaseTodoListTests.setup();
        }

    @BeforeEach
    void startServerAndBrowser() throws Exception
        {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange ->
            {
            byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);
            try (exchange)
                {
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(responseStatus, body.length);
                exchange.getResponseBody().write(body);
                }
            });
        server.start();
        browser = new FirefoxDriver(new FirefoxOptions().addArguments("--headless"));
        }

    @AfterEach
    void stopServerAndBrowser()
        {
        try
            {
            if (browser != null)
                {
                browser.quit();
                }
            }
        finally
            {
            if (server != null)
                {
                server.stop(0);
                }
            }
        }

    @Test
    void shouldRecoverWhenInitialNavigationRemainsBlank()
        {
        AtomicInteger navigations = new AtomicInteger();
        WebDriver driver = decorateBrowser(navigations, true);

        new TestPage(driver, getUrl()).openApplication();

        assertTrue(navigations.get() >= 2, "The uncommitted navigation must be retried");
        assertEquals(getUrl(), browser.getCurrentUrl());
        assertEquals("Coherence To Do Example Application", browser.getTitle());
        assertTrue(browser.findElement(By.cssSelector("input.new-todo")).isDisplayed());
        }

    @Test
    void shouldRejectHttpErrorWithoutRetryingApplicationResponse()
        {
        responseStatus = 404;
        responseBody = "<html><head></head><body>Not found</body></html>";
        assertApplicationResponseRejected();
        }

    @Test
    void shouldRejectPageWithoutTaskInput()
        {
        responseBody = "<html><head><title>Coherence To Do Example Application</title></head>"
                + "<body><div id='root'></div></body></html>";
        assertApplicationResponseRejected();
        }

    private void assertApplicationResponseRejected()
        {
        AtomicInteger responses = new AtomicInteger();
        WebDriver driver = decorateBrowser(responses, false);

        TimeoutException error = assertThrows(TimeoutException.class,
                () -> new TestPage(driver, getUrl()).openApplication());

        assertEquals(1, responses.get(), "An application response must not be retried");
        assertTrue(error.getMessage().contains("Application did not become ready at " + getUrl()));
        assertTrue(error.getMessage().contains("current URL=" + getUrl()));
        }

    private WebDriver decorateBrowser(AtomicInteger navigations, boolean loseFirstNavigation)
        {
        return (WebDriver) Proxy.newProxyInstance(WebDriver.class.getClassLoader(),
                new Class<?>[] {WebDriver.class, JavascriptExecutor.class}, (proxy, method, args) ->
                    {
                    boolean navigation = method.getName().equals("get");
                    if (navigation && loseFirstNavigation && navigations.incrementAndGet() == 1)
                        {
                        // Reproduce Firefox returning successfully without leaving about:blank.
                        return null;
                        }
                    try
                        {
                        Object result = method.invoke(browser, args);
                        if (navigation && !loseFirstNavigation
                                && !"about:blank".equals(browser.getCurrentUrl()))
                            {
                            navigations.incrementAndGet();
                            }
                        return result;
                        }
                    catch (InvocationTargetException error)
                        {
                        throw error.getCause();
                        }
                    });
        }

    private String getUrl()
        {
        return "http://127.0.0.1:" + server.getAddress().getPort() + '/';
        }

    private static class TestPage extends BaseTodoListTests
        {
        private TestPage(WebDriver browser, String url)
            {
            driver = browser;
            this.url = url;
            }

        @Override
        protected String getUrl()
            {
            return url;
            }

        private final String url;
        }

    private HttpServer server;
    private WebDriver browser;
    private volatile int responseStatus = 200;
    private volatile String responseBody = "<html><head><title>Coherence To Do Example Application</title></head>"
            + "<body><input class='new-todo'></body></html>";
    }
