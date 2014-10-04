package org.sauldhernandez;

import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.api.task.*;
import org.apache.commons.io.IOUtils;
import org.apache.maven.shared.invoker.*;

import java.io.*;
import java.util.Arrays;

/**
 * Provides the implementation of the Task extension point, and executes
 * the necessary logic for the operation of the plugin.
 *
 * @author Saúl Hernández
 * @since 1.0
 */
@Extension
public class MavenTaskPlugin implements Task
{
    private static final String GOAL_KEY = "goal";
    private static final String PROPERTIES_KEY = "properties";
    private static final String WORKDIR_KEY = "workdir";

    @Override
    public TaskConfig config() {
        TaskConfig config = new TaskConfig();
        config.addProperty(GOAL_KEY);
        config.addProperty(PROPERTIES_KEY);
        config.addProperty(WORKDIR_KEY).withDefault(".");

        return config;
    }

    @Override
    public TaskExecutor executor() {
        return new TaskExecutor() {
            @Override
            public ExecutionResult execute(TaskConfig taskConfig, TaskExecutionContext context) {

                try {
                    PipedInputStream inOut = new PipedInputStream();
                    PipedInputStream inErr = new PipedInputStream();

                    PrintStream errStream = new PrintStream(new PipedOutputStream(inOut));
                    PrintStream outStream = new PrintStream(new PipedOutputStream(inErr));
                    context.console().readErrorOf(inErr);
                    context.console().readOutputOf(inOut);

                    PrintStream oldOut = System.out;
                    PrintStream oldErr = System.err;

                    System.setOut(outStream);
                    System.setErr(errStream);

                    InvocationRequest request = new DefaultInvocationRequest();
                    request.setGoals(Arrays.asList(taskConfig.getValue(GOAL_KEY).split(" ")));
                    request.setBaseDirectory(new File(context.workingDir(), taskConfig.getValue(WORKDIR_KEY)));

                    Invoker invoker = new DefaultInvoker();
                    invoker.setMavenHome()

                    try {
                        InvocationResult result = invoker.execute(request);
                        if(result.getExitCode() == 0)
                            return ExecutionResult.success("Maven execution completed successfully.");
                        else
                            return ExecutionResult.failure("Maven execution failed.");
                    }
                    catch(MavenInvocationException e) {
                        return ExecutionResult.failure("Failed to execute maven.", e);
                    }
                    finally {
                        System.setOut(oldOut);
                        System.setErr(oldErr);
                    }

                }
                catch(IOException e) {
                    return ExecutionResult.failure("Failed to pipe output", e);
                }
            }
        };
    }

    @Override
    public TaskView view() {
        return new TaskView() {
            @Override
            public String displayValue() {
                return "Maven";
            }

            @Override
            public String template() {
                try {
                    return IOUtils.toString(getClass().getResourceAsStream("/views/task.template.html"), "UTF-8");
                } catch (Exception e) {
                    return "Failed to find template: " + e.getMessage();
                }
            }
        };
    }

    @Override
    public ValidationResult validate(TaskConfig taskConfig) {
        return new ValidationResult();
    }
}
