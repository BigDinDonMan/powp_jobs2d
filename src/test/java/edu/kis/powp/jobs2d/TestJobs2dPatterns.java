package edu.kis.powp.jobs2d;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.kis.legacy.drawer.panel.DefaultDrawerFrame;
import edu.kis.legacy.drawer.panel.DrawPanelController;
import edu.kis.legacy.drawer.shape.LineFactory;
import edu.kis.powp.appbase.Application;
import edu.kis.powp.command.*;
import edu.kis.powp.jobs2d.drivers.adapter.LineDrawerAdapter;
import edu.kis.powp.jobs2d.drivers.adapter.Jobs2dToDrawerAdapter;
import edu.kis.powp.jobs2d.events.SelectChangeVisibleOptionListener;
import edu.kis.powp.jobs2d.events.SelectTestFigureOptionListener;
import edu.kis.powp.jobs2d.features.DrawerFeature;
import edu.kis.powp.jobs2d.features.DriverFeature;

public class TestJobs2dPatterns {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	/**
	 * Setup test concerning preset figures in context.
	 * 
	 * @param application Application context.
	 */
	private static void setupPresetTests(Application application) {
		SelectTestFigureOptionListener selectTestFigureOptionListener = new SelectTestFigureOptionListener(
				DriverFeature.getDriverManager(), TestFigureType.FIGURE_JOE_1);
		SelectTestFigureOptionListener selectTestFigureOptionListener1 = new SelectTestFigureOptionListener(
				DriverFeature.getDriverManager(), TestFigureType.FIGURE_JOE_2);
		application.addTest("Figure Joe 1", selectTestFigureOptionListener);
		application.addTest("Figure Joe 2", selectTestFigureOptionListener1);
		application.addTest("Triangle", e -> {
			DriverCommand command = CommandFactory.getTriangleCommand(DriverFeature.getDriverManager().getCurrentDriver());
			command.execute();
		});
		application.addTest("Rectangle", e -> {
			DriverCommand command = CommandFactory.getRectangleCommand(DriverFeature.getDriverManager().getCurrentDriver());
			command.execute();
		});

		application.addTest("Figure Joe 1 alternative (builder)", e -> {
			Job2dDriver currentDriver = DriverFeature.getDriverManager().getCurrentDriver();
			CommandBuilder cb = new CommandBuilder();
			DriverCommand command = cb.
					addCommand(new SetPositionCommand(currentDriver, -120, -120)).
					addCommand(new OperateToCommand(currentDriver, 120, -120)).
					addCommand(new OperateToCommand(currentDriver, 120, 120)).
					addCommand(new OperateToCommand(currentDriver, -120, 120)).
					addCommand(new OperateToCommand(currentDriver, -120, -120)).
					addCommand(new OperateToCommand(currentDriver, 120, 120)).
					addCommand(new SetPositionCommand(currentDriver, 120, -120)).
					addCommand(new OperateToCommand(currentDriver, -120, 120)).
					create();
			command.execute();
		});
	}

	/**
	 * Setup driver manager, and set default driver for application.
	 * 
	 * @param application Application context.
	 */
	private static void setupDrivers(Application application) {
		Job2dDriver loggerDriver = new LoggerDriver();
		DriverFeature.addDriver("Logger Driver", loggerDriver);
		DriverFeature.getDriverManager().setCurrentDriver(loggerDriver);

		Job2dDriver testDriver = new Jobs2dToDrawerAdapter(DrawerFeature.getDrawerController());
		DriverFeature.addDriver("Doodle Simulator", testDriver);

		Job2dDriver dottedLineDriver = new LineDrawerAdapter(DrawerFeature.getDrawerController(), LineFactory.getDottedLine());
		Job2dDriver basicLineDriver = new LineDrawerAdapter(DrawerFeature.getDrawerController(), LineFactory.getBasicLine());
		Job2dDriver specialLineDriver = new LineDrawerAdapter(DrawerFeature.getDrawerController(), LineFactory.getSpecialLine());
		DriverFeature.addDriver("Dotted line", dottedLineDriver);
		DriverFeature.addDriver("Basic line", basicLineDriver);
		DriverFeature.addDriver("Special line", specialLineDriver);

		DriverFeature.updateDriverInfo();
	}

	/**
	 * Auxiliary routines to enable using Buggy Simulator.
	 * 
	 * @param application Application context.
	 */
	private static void setupDefaultDrawerVisibilityManagement(Application application) {
		DefaultDrawerFrame defaultDrawerWindow = DefaultDrawerFrame.getDefaultDrawerFrame();
		application.addComponentMenuElementWithCheckBox(DrawPanelController.class, "Default Drawer Visibility",
				new SelectChangeVisibleOptionListener(defaultDrawerWindow), true);
		defaultDrawerWindow.setVisible(false);
	}

	/**
	 * Setup menu for adjusting logging settings.
	 * 
	 * @param application Application context.
	 */
	private static void setupLogger(Application application) {
		application.addComponentMenu(Logger.class, "Logger", 0);
		application.addComponentMenuElement(Logger.class, "Clear log",
				(ActionEvent e) -> application.flushLoggerOutput());
		application.addComponentMenuElement(Logger.class, "Fine level", (ActionEvent e) -> logger.setLevel(Level.FINE));
		application.addComponentMenuElement(Logger.class, "Info level", (ActionEvent e) -> logger.setLevel(Level.INFO));
		application.addComponentMenuElement(Logger.class, "Warning level",
				(ActionEvent e) -> logger.setLevel(Level.WARNING));
		application.addComponentMenuElement(Logger.class, "Severe level",
				(ActionEvent e) -> logger.setLevel(Level.SEVERE));
		application.addComponentMenuElement(Logger.class, "OFF logging", (ActionEvent e) -> logger.setLevel(Level.OFF));
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Application app = new Application("2d jobs Visio");
				DrawerFeature.setupDrawerPlugin(app);
				setupDefaultDrawerVisibilityManagement(app);

				DriverFeature.setupDriverPlugin(app);
				setupDrivers(app);
				setupPresetTests(app);
				setupLogger(app);

				app.setVisibility(true);
			}
		});
	}

}
