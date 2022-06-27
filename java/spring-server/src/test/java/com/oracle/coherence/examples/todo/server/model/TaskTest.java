package com.oracle.coherence.examples.todo.server.model;

import com.tangosol.io.pof.ConfigurablePofContext;
import com.tangosol.util.Binary;
import com.tangosol.util.ExternalizableHelper;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class TaskTest
    {
    @Test
    public void shouldImplementPortableObject()
        {
        Task task = new Task("test task");
        task.setCompleted(true);

        ConfigurablePofContext pofContext = new ConfigurablePofContext();
        Binary binary = ExternalizableHelper.toBinary(task, pofContext);
        Task result = ExternalizableHelper.fromBinary(binary, pofContext);
        assertThat(result, is(notNullValue()));
        assertThat(result.getDescription(), is(task.getDescription()));
        assertThat(result.getCreatedAt(), is(task.getCreatedAt()));
        assertThat(result.getCompleted(), is(task.getCompleted()));
        }
    }
