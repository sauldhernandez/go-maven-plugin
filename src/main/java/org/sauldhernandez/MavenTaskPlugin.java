package org.sauldhernandez;

import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.config.Option;
import com.thoughtworks.go.plugin.api.config.Property;
import com.thoughtworks.go.plugin.api.response.execution.ExecutionResult;
import com.thoughtworks.go.plugin.api.response.validation.ValidationResult;
import com.thoughtworks.go.plugin.api.task.*;

/**
 * Hello world!
 *
 */
@Extension
public class MavenTaskPlugin implements Task
{
    private static final String GOAL_KEY = "goal";
    private static final String PROPERTIES_KEY = "properties";

    @Override
    public TaskConfig config() {
        TaskConfig config = new TaskConfig();
        config.addProperty(GOAL_KEY);
        config.addProperty(PROPERTIES_KEY);

        return config;
    }

    @Override
    public TaskExecutor executor() {
        return new TaskExecutor() {
            @Override
            public ExecutionResult execute(TaskConfig taskConfig, TaskExecutionContext taskExecutionContext) {
                return ExecutionResult.success("Done nothing...");
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
                return  "<div class=\"form_item_block\">" +
                        "   <label>Goal:<span class=\"asterisk\">*</span>" +
                        "       <input type=\"text\" ng-model\"" + GOAL_KEY + "\" ng-required=\"true\"/>"+
                        "   </label>" +
                        "</div>";

            }
        };
    }

    @Override
    public ValidationResult validate(TaskConfig taskConfig) {
        return new ValidationResult();
    }
}
