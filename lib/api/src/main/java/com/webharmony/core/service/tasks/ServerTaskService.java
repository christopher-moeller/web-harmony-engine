package com.webharmony.core.service.tasks;

import com.webharmony.core.context.ContextHolder;
import com.webharmony.core.data.jpa.model.AppServerTask;
import com.webharmony.core.i18n.I18N;
import com.webharmony.core.i18n.I18nTranslation;
import com.webharmony.core.service.data.ServerTaskCrudService;
import com.webharmony.core.service.tasks.utils.AbstractServerTaskHandler;
import com.webharmony.core.service.tasks.utils.ServerTask;
import com.webharmony.core.service.tasks.utils.Task;
import com.webharmony.core.utils.assertions.Assert;
import com.webharmony.core.utils.exceptions.NotFoundException;
import com.webharmony.core.utils.reflection.ReflectionUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ServerTaskService implements I18nTranslation {

    private final I18N i18N = createI18nInstance(ServerTaskService.class);

    private List<ServerTask> serverTasks;

    private final ServerTaskCrudService serverTaskCrudService;

    public ServerTaskService(ServerTaskCrudService serverTaskCrudService) {
        this.serverTaskCrudService = serverTaskCrudService;
    }

    public void init() {
        this.serverTasks = generateServerTasks();
        validateServerTasks();
        persistServerTasks();
    }

    private void validateServerTasks() {
        for (ServerTask serverTask : this.serverTasks) {
            List<ServerTask> allTasksWithCurrentId = serverTasks.stream()
                    .filter(t -> t.getTaskId().equals(serverTask.getTaskId()))
                    .toList();

            Assert.hasSize(allTasksWithCurrentId, 1).verify();
        }
    }

    private List<ServerTask> generateServerTasks() {

        final List<ServerTask> resultList = new ArrayList<>();

        List<AbstractServerTaskHandler> handlers = ReflectionUtils.getAllProjectClassesImplementingSuperClass(AbstractServerTaskHandler.class)
                .stream()
                .map(this::getOrCreateHandler)
                .toList();

        for (AbstractServerTaskHandler handler : handlers) {
            resultList.addAll(generateTasksByHandler(handler));
        }

        return resultList;
    }

    private AbstractServerTaskHandler getOrCreateHandler(Class<? extends AbstractServerTaskHandler> handlerClass) {
        return ContextHolder.getContext().getBeanIfExists(handlerClass)
                .map(AbstractServerTaskHandler.class::cast)
                .orElseGet(() -> ReflectionUtils.createNewInstanceWithEmptyConstructor(handlerClass));
    }

    private List<ServerTask> generateTasksByHandler(AbstractServerTaskHandler handler) {

        List<ServerTask> tasks = new ArrayList<>();

        List<Method> methods = ReflectionUtils.getAllMethods(handler.getClass(), true);
        for (Method method : methods) {
            Task task = AnnotationUtils.getAnnotation(method, Task.class);
            if(task == null)
                continue;

            ServerTask serverTask = new ServerTask();
            serverTask.setHandlerName(handler.getHandlerName());
            serverTask.setTaskId(createServerTaskIdByHandlerAndMethod(handler, method));
            serverTask.setTaskHandler(handler);
            serverTask.setMethod(method);
            serverTask.setIsRequired(task.isRequired());
            serverTask.setTaskName(task.name());

            tasks.add(serverTask);
        }

        return tasks;
    }

    private String createServerTaskIdByHandlerAndMethod(AbstractServerTaskHandler handler, Method method) {
        String handlerPart = handler.getClass().getName();
        if(handlerPart.contains("$"))
            handlerPart = handler.getClass().getSuperclass().getName();
        String methodPart = method.getName();
        return handlerPart + ":" + methodPart;
    }

    private void persistServerTasks() {
        for (ServerTask serverTask : this.serverTasks) {
            if(serverTaskCrudService.findByTaskId(serverTask.getTaskId()).isPresent())
                continue;

            AppServerTask entity = new AppServerTask();
            entity.setTaskId(serverTask.getTaskId());
            entity.setTaskName(serverTask.getTaskName());
            entity.setIsRequired(serverTask.getIsRequired());
            serverTaskCrudService.saveEntity(entity);
        }

        serverTaskCrudService.getAllEntities()
                .stream()
                .filter(entity -> this.serverTasks.stream().noneMatch(task -> task.getTaskId().equals(entity.getTaskId())))
                .forEach(serverTaskCrudService::deleteEntity);

    }

    public void executeTaskByUUID(UUID uuid) {
        AppServerTask appServerTask = serverTaskCrudService.getEntityById(uuid);
        serverTasks.stream()
                .filter(t -> t.getTaskId().equals(appServerTask.getTaskId()))
                .findAny()
                .orElseThrow(() -> new NotFoundException(i18N.translate("ServerTask with id '{id}' does not exist").add("id", uuid).build()))
                .execute();
    }
}
