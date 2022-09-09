package io.automatiko.examples;

import io.automatiko.engine.api.Workflows;
import io.automatiko.engine.app.rest.model.User;
import io.automatiko.engine.workflow.base.core.context.variable.Variable;
import io.automatiko.engine.workflow.builder.RestServiceNodeBuilder;
import io.automatiko.engine.workflow.builder.ServiceNodeBuilder;
import io.automatiko.engine.workflow.builder.SplitNodeBuilder;
import io.automatiko.engine.workflow.builder.WorkflowBuilder;
import io.automatiko.examples.registration.service.NotificationService;
import io.automatiko.examples.registration.service.UserService;

@Workflows
public class UserRegistrationWorkflow {

    /**
     * Create workflow definition to be used to perform user registrations
     * 
     * @return the workflow definition
     */
    public WorkflowBuilder build() {
        // create new workflow
        WorkflowBuilder builder = WorkflowBuilder.newWorkflow("userRegistration", "User registration workflow");
        // define data objects that will be used within the workflow
        User user = builder.dataObject(User.class, "user");
        Boolean valid = builder.dataObject(Boolean.class, "valid", Variable.INTERNAL_TAG);
        // create starting point
        builder.start("start").then();
        // then create validation service
        ServiceNodeBuilder validateUserService = builder.service("Validate user");
        // UserService.isValid will be invoked with taking the 'user' data object as argument
        // that will assign the output of the validation (boolean) to a 'valid' data object
        validateUserService.toDataObject("valid",
                validateUserService.type(UserService.class).isValid(validateUserService.fromDataObject("user")));

        // next let's evaluate the outcome of the validation
        SplitNodeBuilder isValidSplit = validateUserService.thenSplit("is valid");
        // if it is not valid then go another path
        // first use notification service to provide information about submitted data being not valid
        ServiceNodeBuilder notValidNotifyService = isValidSplit
                .when(() -> !valid)
                .service("Notify about user not valid");

        notValidNotifyService.type(NotificationService.class)
                .sendInvalidDataNotification(notValidNotifyService.fromDataObject("user"));
        // and then end the workflow
        notValidNotifyService.then().end("User info not valid");

        // in case the user data is valid then proceed to generate user name and password via UserService.assignUserIdAndPassword
        // where the 'user' data object is used as argument
        ServiceNodeBuilder generateInfoService = isValidSplit
                .when(() -> valid)
                .service("Generate user name and password");
        generateInfoService.type(UserService.class).assignUserIdAndPassword(generateInfoService.fromDataObject("user"));

        // once username and password is generated call a Swagger PetStore REST api to get the user
        RestServiceNodeBuilder getUserRestService = generateInfoService.then().restService("Get user")
                .openApi("api/swagger.json").operation("getUserByName")
                .expressionAsInput("username", String.class, () -> user.getUsername());

        // in case service returns successfully (200) then it will go to notification service to ntify that user is already
        // registered as it was found in the REST service
        ServiceNodeBuilder notifyAlreadyRegistered = getUserRestService.then().service("notify already registered");
        notifyAlreadyRegistered.type(NotificationService.class)
                .sendAlreadyRegisteredNotification(notifyAlreadyRegistered.fromDataObject("user"));
        // and end the workflow
        notifyAlreadyRegistered.then().end("Already registered");

        // in case the user was not found (returns 404) then proceed to the next rest service to create the user
        RestServiceNodeBuilder createUserRestService = getUserRestService.onError("404").then().restService("Create user")
                .openApi("api/swagger.json")
                .operation("createUser").fromDataObject("user");
        // in case create user was successful then continue to notification service to send notification about successful registration 
        ServiceNodeBuilder notifySuccessfullyRegistered = createUserRestService.then().service("notify already registered");

        notifySuccessfullyRegistered.type(NotificationService.class)
                .sendSuccessNotification(notifySuccessfullyRegistered.fromDataObject("user"));
        // and then end the workflow
        notifySuccessfullyRegistered.then().end("Already registered");

        // in case create user causes server errors (returns 500) then simply end the workflow
        createUserRestService.onError("500").then().end("Server error");

        return builder;
    }
}
