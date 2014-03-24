/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer.project;

import com.eas.designer.application.project.ClientType;
import com.eas.designer.application.project.AppServerType;
import com.eas.client.application.PlatypusClientApplication;
import com.eas.client.resourcepool.DatasourcesArgsConsumer;
import com.eas.designer.application.project.PlatypusProject;
import com.eas.designer.application.project.PlatypusProjectSettings;
import com.eas.designer.debugger.DebuggerEnvironment;
import com.eas.designer.debugger.DebuggerUtils;
import com.eas.designer.explorer.j2ee.PlatypusWebModuleManager;
import com.eas.designer.application.platform.EmptyPlatformHomePathException;
import com.eas.designer.application.platform.PlatypusPlatform;
import com.eas.designer.explorer.platform.PlatypusPlatformDialog;
import com.eas.designer.explorer.server.PlatypusServerInstance;
import com.eas.designer.explorer.server.PlatypusServerInstanceProvider;
import com.eas.designer.explorer.server.ServerState;
import com.eas.designer.explorer.server.ServerSupport;
import com.eas.server.PlatypusServer;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.netbeans.api.db.explorer.ConnectionManager;
import org.netbeans.api.db.explorer.DatabaseConnection;
import org.netbeans.api.extexecution.ExecutionDescriptor;
import org.netbeans.api.extexecution.ExecutionService;
import org.netbeans.api.extexecution.ExternalProcessBuilder;
import org.openide.ErrorManager;
import org.openide.actions.SaveAllAction;
import org.openide.awt.HtmlBrowser;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;

/**
 *
 * @author mg
 */
public class ProjectRunner {

    public static final String JVM_RUN_COMMAND_NAME = "java"; //NOI18N
    public static final String OPTION_PREFIX = PlatypusClientApplication.CMD_SWITCHS_PREFIX;
    public static final String CLASSPATH_OPTION_NAME = "cp"; //NOI18N
    private static final String EXT_DIRECTORY_NAME = "ext"; //NOI18N
    private static final String CLIENT_APP_NAME = "Application.jar"; //NOI18N
    private static final String JMX_AUTHENTICATE_OPTION_NAME = "Dcom.sun.management.jmxremote.authenticate"; //NOI18N
    private static final String JMX_SSL_OPTION_NAME = "Dcom.sun.management.jmxremote.ssl"; //NOI18N
    private static final String JMX_REMOTE_OPTION_NAME = "Dcom.sun.management.jmxremote"; //NOI18N
    private static final String JMX_REMOTE_OPTION_PORT_NAME = "Dcom.sun.management.jmxremote.port"; //NOI18N
    private static final String LOG_LEVEL_OPTION_NAME = "D.level"; //NOI18N
    private static final String LOG_HANDLERS_OPTION_NAME = "Dhandlers"; //NOI18N
    private static final String CONSOLE_LOG_HANDLER_NAME = "java.util.logging.ConsoleHandler"; //NOI18N
    private static final String CONSOLE_LOG_HANDLER_LEVEL_OPION_NAME = "Djava.util.logging.ConsoleHandler.level"; //NOI18N
    private static final String CONSOLE_LOG_FORMATTER_OPTION_NAME = "Djava.util.logging.ConsoleHandler.formatter"; //NOI18N
    private static final String LOG_CONFIG_CLASS_OPTION_NAME = "Djava.util.logging.config.class"; //NOI18N
    private static final String JS_APPLICATION_LOG_LEVEL_OPTION_NAME = "DApplication.level"; //NOI18N
    private static final String EQUALS_SIGN = "="; //NOI18N
    private static final String FALSE = "false"; //NOI18N
    private static final String LOCAL_HOSTNAME = "localhost"; //NOI18N
    private static final int DEBUGGER_CONNECT_MAX_ATTEMPTS = 10;

    protected static void saveAll() {
        SaveAllAction action = SystemAction.get(SaveAllAction.class);
        if (action != null) {
            action.performAction();
        }
    }

    /**
     * Starts an application in run mode.
     *
     * @param project Application's project.
     * @param appElementId Application element's name OR relative path to the
     * executable file.
     * @throws Exception If something goes wrong.
     */
    public static void run(final PlatypusProject project, final String appElementId) throws Exception {
        saveAll();
        project.getRequestProcessor().post(new Runnable() {
            @Override
            public void run() {
                start(project, appElementId, false);
            }
        });
    }

    /**
     * Starts an application in debug mode.
     *
     * @param project Application's project.
     * @param appElementId Application element's name OR relative path to the
     * executable file.
     * @throws Exception If something goes wrong.
     */
    public static void debug(final PlatypusProject project, final String appElementId) throws Exception {
        saveAll();
        project.getRequestProcessor().post(new Runnable() {
            @Override
            public void run() {
                Future<Integer> runningProgram = start(project, appElementId, true);
                if (runningProgram != null) {
                    try {
                        DebuggerEnvironment clientEnv = new DebuggerEnvironment(project);
                        clientEnv.host = LOCAL_HOSTNAME;
                        clientEnv.port = project.getSettings().getDebugClientPort();
                        clientEnv.runningProgram = runningProgram;
                        clientEnv.runningElement = project.getSettings().getRunElement();
                        DebuggerUtils.attachDebugger(clientEnv, DEBUGGER_CONNECT_MAX_ATTEMPTS);
                        project.getOutputWindowIO().getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Client_Debug_Activated"));//NOI18N
                        if (project.getSettings().getRunAppServerType() == AppServerType.PLATYPUS_SERVER) {
                            try {
                                DebuggerEnvironment serverEnv = new DebuggerEnvironment(project);
                                serverEnv.host = LOCAL_HOSTNAME;
                                serverEnv.port = project.getSettings().getDebugServerPort();
                                serverEnv.runningProgram = null;
                                serverEnv.runningElement = null;
                                DebuggerUtils.attachDebugger(serverEnv, DEBUGGER_CONNECT_MAX_ATTEMPTS);
                                project.getOutputWindowIO().getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Server_Debug_Activated"));//NOI18N
                            } catch (Exception ex) {
                                ErrorManager.getDefault().notify(ex);
                            }
                        }
                    } catch (Exception ex) {
                        ErrorManager.getDefault().notify(ex);
                    }
                }
            }
        });
    }

    private static Future<Integer> start(PlatypusProject project, String appElementId, boolean debug) {
        InputOutput io = IOProvider.getDefault().getIO(project.getDisplayName(), false);
        File binDir;
        try {
            binDir = PlatypusPlatform.getPlatformBinDirectory();
        } catch (EmptyPlatformHomePathException | IllegalStateException ex) {
            io.getErr().println(ex.getMessage());
            if (!PlatypusPlatformDialog.showPlatformHomeDialog()) {
                return null;
            } else {
                try {
                    binDir = PlatypusPlatform.getPlatformBinDirectory();
                } catch (EmptyPlatformHomePathException | IllegalStateException ex1) {
                    io.getErr().println(ex1.getMessage());
                    io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Specify_Platypus_Platform_Path"));//NOI18N
                    return null;
                }
            }
        }
        PlatypusProjectSettings pps = project.getSettings();
        io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Application_Starting"));
        String appUrl = null;
        if (!pps.isNotStartServer()) {
            if (AppServerType.PLATYPUS_SERVER.equals(pps.getRunAppServerType())) {
                PlatypusServerInstance serverInstance = PlatypusServerInstanceProvider.getPlatypusDevServer();
                if (serverInstance.getServerState() == ServerState.STOPPED) {
                    io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Starting_Platypus_Server"));//NOI18N
                    if (serverInstance.start(project, binDir, debug)) {
                        io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Platypus_Server_Started"));//NOI18N
                        try {
                            io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Waiting_Platypus_Server"));//NOI18N
                            ServerSupport ss = new ServerSupport(serverInstance);
                            ss.waitForServer(LOCAL_HOSTNAME, pps.getServerPort());
                            PlatypusServerInstanceProvider.getPlatypusDevServer().setServerState(ServerState.RUNNING);
                        } catch (ServerSupport.ServerTimeOutException | ServerSupport.ServerStoppedException | InterruptedException ex) {
                            io.getErr().println(ex.getMessage());
                            return null;
                        }
                    } else {
                        io.getErr().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Cnt_Start_Platypus_Server"));//NOI18N
                        return null;
                    }
                } else {
                    assert serverInstance.getProject() != null;
                    if (serverInstance.getProject().getProjectDirectory().equals(project.getProjectDirectory())) {
                        io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Platypus_Server_Already_Started"));//NOI18N
                    } else {
                        io.getErr().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Platypus_Server_Started_Another_Project") + serverInstance.getProject().getDisplayName());//NOI18N
                    }
                }
            } else if (AppServerType.J2EE_SERVER.equals(pps.getRunAppServerType())) {
                io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Deploying_J2EE_Container"));//NOI18N
                PlatypusWebModuleManager webManager = project.getLookup().lookup(PlatypusWebModuleManager.class);
                if (webManager != null) {
                    appUrl = webManager.run(appElementId, debug);
                } else {
                    throw new IllegalStateException("An instance of PlatypusWebModuleManager is not found in project's lookup.");
                }
            }
        }
        if (ClientType.PLATYPUS_CLIENT.equals(pps.getRunClientType())) {
            ExecutionDescriptor descriptor = new ExecutionDescriptor()
                    .frontWindow(true)
                    .controllable(true);
            List<String> arguments = new ArrayList<>();
            if (pps.getRunClientVmOptions() != null && !pps.getRunClientVmOptions().isEmpty()) {
                addArguments(arguments, pps.getRunClientVmOptions());
                io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_VM_Run_Options"), pps.getRunClientVmOptions()));//NOI18N
            }
            if (debug) {
                setDebugArguments(arguments, project.getSettings().getDebugClientPort());
            }

            io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_Logging_Level"), project.getSettings().getClientLogLevel()));//NOI18N
            setLogging(arguments, project.getSettings().getClientLogLevel());

            arguments.add(OPTION_PREFIX + CLASSPATH_OPTION_NAME);
            arguments.add(getExtendedClasspath(getExecutablePath(binDir)));

            arguments.add(PlatypusClientApplication.class.getName());

            String runElementId = null;

            if (appElementId != null && !appElementId.isEmpty()) {
                runElementId = appElementId;
            } else if (pps.getRunElement() != null && !pps.getRunElement().isEmpty()) {
                runElementId = pps.getRunElement();
            }
            if (runElementId != null && !runElementId.isEmpty()) {
                arguments.add(OPTION_PREFIX + PlatypusClientApplication.APPELEMENT_CMD_SWITCH);
                arguments.add(runElementId);
                io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Start_App_Element") + runElementId); //NOI18N
            } else {
                io.getErr().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Start_App_Element_Not_Set")); //NOI18N
                return null;
            }
            if (AppServerType.NONE.equals(pps.getRunAppServerType())) {
                // Iterate through all datasources, registered in the designer.
                // Apply them as datasources in considered server.
                DatabaseConnection defaultDatabaseConnection = null;
                DatabaseConnection[] dataSources = ConnectionManager.getDefault().getConnections();
                for (DatabaseConnection connection : dataSources) {
                    if (isConnectionValid(connection)) {
                        if (connection.getDisplayName() == null ? pps.getDefaultDataSourceName()== null : connection.getDisplayName().equals(pps.getDefaultDataSourceName())) {
                            defaultDatabaseConnection = connection;
                        }
                        arguments.add(ProjectRunner.OPTION_PREFIX + DatasourcesArgsConsumer.DB_RESOURCE_CONF_PARAM);
                        arguments.add(connection.getDisplayName());// Hack because of NetBeans
                        arguments.add(ProjectRunner.OPTION_PREFIX + DatasourcesArgsConsumer.DB_URL_CONF_PARAM);
                        arguments.add(connection.getDatabaseURL());
                        arguments.add(ProjectRunner.OPTION_PREFIX + DatasourcesArgsConsumer.DB_USERNAME_CONF_PARAM);
                        arguments.add(connection.getUser());
                        arguments.add(ProjectRunner.OPTION_PREFIX + DatasourcesArgsConsumer.DB_PASSWORD_CONF_PARAM);
                        arguments.add(connection.getPassword());
                        if (connection.getSchema() != null && !connection.getSchema().isEmpty()) {
                            arguments.add(ProjectRunner.OPTION_PREFIX + DatasourcesArgsConsumer.DB_SCHEMA_CONF_PARAM);
                            arguments.add(connection.getSchema());
                        }
                    } else {
                        io.getErr().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Invalid_Database", connection.getDisplayName()));
                    }
                }

                io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Database_Direct"));//NOI18N
                if (defaultDatabaseConnection != null) {
                    arguments.add(ProjectRunner.OPTION_PREFIX + PlatypusClientApplication.DEF_DATASOURCE_CONF_PARAM);
                    arguments.add(pps.getDefaultDataSourceName());
                } else if (pps.getDefaultDataSourceName()!= null && !pps.getDefaultDataSourceName().isEmpty()) {
                    io.getErr().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Missing_App_Database"));
                }

                if (project.getSettings().isDbAppSources()) {
                    if (defaultDatabaseConnection != null) {
                        arguments.add(ProjectRunner.OPTION_PREFIX + PlatypusClientApplication.URL_CMD_SWITCH);
                        arguments.add("jndi://" + pps.getDefaultDataSourceName());
                        io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_App_Sources_Database"));//NOI18N
                    } else {
                        io.getErr().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Missing_App_Database"));
                    }
                } else {
                    arguments.add(ProjectRunner.OPTION_PREFIX + PlatypusClientApplication.URL_CMD_SWITCH);
                    arguments.add(project.getProjectDirectory().toURI().toASCIIString());
                    io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_App_Sources"), project.getProjectDirectory().toURI().toASCIIString()));//NOI18N
                }

                if (project.getSettings().isJ2SEAnonymousAccessEnabled()) {
                    arguments.add(ProjectRunner.OPTION_PREFIX + PlatypusClientApplication.ANONYMOUS_ON_CMD_SWITCH);
                }

            } else {
                if (pps.isNotStartServer()) {
                    appUrl = pps.getClientUrl();
                } else if (AppServerType.PLATYPUS_SERVER.equals(pps.getRunAppServerType())) {
                    appUrl = getDevPlatypusServerUrl(pps);
                }
                if (appUrl != null && !appUrl.isEmpty()) {
                    arguments.add(OPTION_PREFIX + PlatypusClientApplication.URL_CMD_SWITCH);
                    arguments.add(appUrl);
                    io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_App_Server_URL"), appUrl));//NOI18N
                } else {
                    io.getErr().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Cnt_Start_Platypus_Client"));//NOI18N
                    return null;
                }
            }
            if (project.getSettings().getRunUser() != null && !project.getSettings().getRunUser().trim().isEmpty() && project.getSettings().getRunPassword() != null && !project.getSettings().getRunPassword().trim().isEmpty()) {
                arguments.add(OPTION_PREFIX + PlatypusClientApplication.USER_CMD_SWITCH);
                arguments.add(project.getSettings().getRunUser());
                arguments.add(OPTION_PREFIX + PlatypusClientApplication.PASSWORD_CMD_SWITCH);
                arguments.add(project.getSettings().getRunPassword());
                io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Login_As_User") + project.getSettings().getRunUser());//NOI18N
            }
            if (pps.getRunClientOptions() != null && !pps.getRunClientOptions().isEmpty()) {
                addArguments(arguments, pps.getRunClientOptions());
                io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_Run_Options"), pps.getRunClientOptions()));//NOI18N
            }
            if (debug) {
                arguments.add(OPTION_PREFIX + PlatypusClientApplication.STOP_BEFORE_RUN_CMD_SWITCH);
            }
            ExternalProcessBuilder processBuilder = new ExternalProcessBuilder(JVM_RUN_COMMAND_NAME);
            for (String argument : arguments) {
                processBuilder = processBuilder.addArgument(argument);
            }
            ExecutionService service = ExecutionService.newService(processBuilder, descriptor, getServiceDisplayName(project, debug));
            io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Starting_Platypus_Client"));//NOI18N
            io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Command_Line") + getCommandLineStr(arguments));//NOI18N
            Future<Integer> clientTask = service.run();
            if (clientTask != null) {
                io.getOut().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Platypus_Client_Started"));//NOI18N
                io.getOut().println();
            }
            return clientTask;
        } else if (ClientType.WEB_BROWSER.equals(pps.getRunClientType())) {
            try {
                if (pps.isNotStartServer()) {
                    appUrl = pps.getClientUrl();
                }
                if (appUrl != null && !appUrl.isEmpty()) {
                    io.getOut().println(String.format(NbBundle.getMessage(ProjectRunner.class, "MSG_Starting_Web_Browser"), appUrl));//NOI18N
                    HtmlBrowser.URLDisplayer.getDefault().showURL(new URL(appUrl));
                } else {
                    io.getErr().println(NbBundle.getMessage(ProjectRunner.class, "MSG_Cnt_Start_Web_Browser"));//NOI18N
                    return null;
                }
            } catch (MalformedURLException ex) {
                ErrorManager.getDefault().notify(ex);
            }
            return null;
        }
        return null;
    }

    public static void addArguments(List<String> arguments, String argsStr) {
        String[] options = argsStr.split(" ");//NOI18N
        if (options.length > 0) {
            for (int i = 0; i < options.length; i++) {
                arguments.add(options[i]);
            }
        }
    }

    public static String getCommandLineStr(List<String> arguments) {
        StringBuilder sb = new StringBuilder(JVM_RUN_COMMAND_NAME);
        for (String argument : arguments) {
            sb.append(" "); //NOI18N
            sb.append(argument);
        }
        return sb.toString();
    }

    public static void setLogging(List<String> arguments, Level logLevel) {
        arguments.add(OPTION_PREFIX
                + LOG_LEVEL_OPTION_NAME
                + EQUALS_SIGN
                + Level.SEVERE.getName());
        arguments.add(OPTION_PREFIX
                + LOG_HANDLERS_OPTION_NAME
                + EQUALS_SIGN
                + CONSOLE_LOG_HANDLER_NAME);
        arguments.add(OPTION_PREFIX
                + CONSOLE_LOG_HANDLER_LEVEL_OPION_NAME
                + EQUALS_SIGN
                + logLevel.getName());
        arguments.add(OPTION_PREFIX
                + JS_APPLICATION_LOG_LEVEL_OPTION_NAME
                + EQUALS_SIGN
                + logLevel.getName());
        arguments.add(OPTION_PREFIX
                + CONSOLE_LOG_FORMATTER_OPTION_NAME
                + EQUALS_SIGN
                + com.eas.util.logging.PlatypusFormatter.class.getName());
        arguments.add(OPTION_PREFIX
                + LOG_CONFIG_CLASS_OPTION_NAME
                + EQUALS_SIGN
                + com.eas.util.logging.LoggersConfig.class.getName());
    }

    public static void setDebugArguments(List<String> arguments, int port) {
        arguments.add(OPTION_PREFIX
                + JMX_AUTHENTICATE_OPTION_NAME
                + EQUALS_SIGN
                + FALSE);
        arguments.add(OPTION_PREFIX
                + JMX_SSL_OPTION_NAME
                + EQUALS_SIGN
                + FALSE);
        arguments.add(OPTION_PREFIX
                + JMX_REMOTE_OPTION_NAME);
        arguments.add(OPTION_PREFIX
                + JMX_REMOTE_OPTION_PORT_NAME
                + EQUALS_SIGN
                + port);
    }

    public static String getExtendedClasspath(String executablePath) {
        StringBuilder classpathStr = new StringBuilder(executablePath);
        File extDir = getPlatformExtDirectory();
        if (extDir.exists() && extDir.isDirectory()) {
            classpathStr.append(File.pathSeparator);
            classpathStr.append(extDir);
            classpathStr.append(File.pathSeparator);
            classpathStr.append(String.format("%s/*", extDir)); //NOI18N
        }
        return classpathStr.toString();
    }

    public static boolean isSetByOption(String command, String options) {
        return options != null && options.contains(OPTION_PREFIX + command);
    }

    public static boolean isConnectionValid(DatabaseConnection connection) {
        return connection.getDisplayName() != null && !connection.getDisplayName().isEmpty() && !connection.getDisplayName().contains(" ")
                && connection.getDatabaseURL() != null && !connection.getDatabaseURL().isEmpty()
                && connection.getUser() != null && !connection.getUser().isEmpty()
                && connection.getPassword() != null && !connection.getPassword().isEmpty();
    }

    private static String getServiceDisplayName(PlatypusProject project, boolean debug) {
        return String.format("%s (%s)", project.getDisplayName(), //NOI18N
                debug ? NbBundle.getMessage(ProjectRunner.class, "LBL_DebugTab_Name") //NOI18N
                : NbBundle.getMessage(ProjectRunner.class, "LBL_RunTab_Name")); //NOI18N
    }

    private static File getPlatformExtDirectory() {
        assert PlatypusPlatform.getPlatformHomePath() != null;
        assert !PlatypusPlatform.getPlatformHomePath().isEmpty();
        File platformHomeDir = new File(PlatypusPlatform.getPlatformHomePath());
        File extDir = new File(platformHomeDir, EXT_DIRECTORY_NAME);
        return extDir;
    }

    private static String getExecutablePath(File aBinDir) {
        File clientAppExecutable = new File(aBinDir, CLIENT_APP_NAME);
        if (!clientAppExecutable.exists()) {
            throw new IllegalStateException("Platypus Client application executable not exists.");
        }
        return clientAppExecutable.getAbsolutePath();
    }

    private static String getDevPlatypusServerUrl(PlatypusProjectSettings pps) {
        return String.format("%s://%s:%s", PlatypusServer.DEFAULT_PROTOCOL, LOCAL_HOSTNAME, pps.getServerPort()); //NOI18N
    }
}
