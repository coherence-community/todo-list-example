/*
 * Copyright (c) 2026, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */
package com.oracle.coherence.examples.todo.test.common;

import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * Test-only JAAS authentication for the hardened Coherence metrics endpoint.
 * This class is in test-common, which applications depend on with test scope.
 */
public class MetricsAuthentication implements LoginModule
    {
    public static final String USER = "metrics-test";
    public static final String PASSWORD = "metrics-test-password";

    /**
     * Install a login context for Coherence HTTP authentication in this test JVM.
     */
    public static void configure()
        {
        Configuration previous = Configuration.getConfiguration();
        Configuration.setConfiguration(new Configuration()
            {
            @Override
            public AppConfigurationEntry[] getAppConfigurationEntry(String name)
                {
                return "CoherenceREST".equals(name)
                        ? new AppConfigurationEntry[] {new AppConfigurationEntry(
                                MetricsAuthentication.class.getName(),
                                AppConfigurationEntry.LoginModuleControlFlag.REQUIRED,
                                Map.of())}
                        : previous.getAppConfigurationEntry(name);
                }
            });
        }

    @Override
    public void initialize(Subject subject, CallbackHandler handler,
                           Map<String, ?> sharedState, Map<String, ?> options)
        {
        this.handler = handler;
        }

    @Override
    public boolean login() throws LoginException
        {
        NameCallback name = new NameCallback("user");
        PasswordCallback password = new PasswordCallback("password", false);
        try
            {
            handler.handle(new Callback[] {name, password});
            if (!USER.equals(name.getName()) ||
                    !java.util.Arrays.equals(PASSWORD.toCharArray(), password.getPassword()))
                {
                throw new LoginException("Invalid test metrics credentials");
                }
            return true;
            }
        catch (LoginException e)
            {
            throw e;
            }
        catch (Exception e)
            {
            throw new LoginException(e.toString());
            }
        finally
            {
            password.clearPassword();
            }
        }

    @Override
    public boolean commit()
        {
        return true;
        }

    @Override
    public boolean abort()
        {
        return true;
        }

    @Override
    public boolean logout()
        {
        return true;
        }

    private CallbackHandler handler;
    }
