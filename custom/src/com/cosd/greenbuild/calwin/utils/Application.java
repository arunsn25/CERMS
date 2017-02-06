package com.cosd.greenbuild.calwin.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.documentum.fc.common.DfException;

/**
 * This is a generic command line application helper. With it command line apps
 * and arguments can be easily defined. It can be used similarly to:
 *
 * <pre>
 * public class MyApp extends Application {
 *
 * 	Argument smellArg, typeArg;
 *
 * 	public MyApp() {
 * 		super();
 * 		this.typeArg = addArg(&quot;--type&quot;, &quot;dog&quot;, &quot;Action object&quot;);
 * 		this.smellArg = addArg(&quot;--smell&quot;, &quot;awful&quot;, &quot;How does it smell&quot;);
 * 	}
 *
 * 	protected int doRun() {
 * 		log.info(&quot;My &quot; + typeArg.getValue() + &quot; has no nose.\n&quot;
 * 				+ &quot;How does it smell?\n&quot; + smellArg.getValue() + &quot;!&quot;);
 * 		return 0;
 * 	}
 *
 * 	public static void main(String[] args) {
 * 		new MyApp().run(args);
 * 	}
 * }
 *
 * </pre>
 *
 * @author Andy.Taylor
 *
 */
public abstract class Application {

	public class Argument {

		private final String name;
		private String defaultVal;
		private final String desc;
		private boolean singleton = false;
		private boolean wasProvided = false;
		private boolean isPassword = false;

		public Argument(String name, String defaultVal, String desc) {
			this.name = name;
			this.defaultVal = defaultVal;
			this.desc = desc;
			if (this.name.toLowerCase().contains("pass"))
				this.isPassword = true;
		}

		public String getName() {
			return name;
		}

		/**
		 * Call this method if the argument has no parameters
		 */
		public void setIsSingleton() {
			this.singleton = true;
		}

		/**
		 * Set to true for the argument to be treated as a password.  Passwords are
		 * crypted, and the crypt can be produced by specifying the --encrypt argument.
		 * @param isPass
		 */
		public void setIsPassword(boolean isPass) {
			this.isPassword = isPass;
		}

		public String getValue() {
			for (int i = 0; i < runArgs.length; i++) {
				if (runArgs[i].equals(name)) {
					if (!singleton) {
						if (i + 1 < runArgs.length) {
							this.wasProvided = true;
							String val = runArgs[i + 1];
							if (isPassword)
								val = decrypt(val);
							return val;
						} else
							throw new IllegalArgumentException("Expected value for " + name);
					}
				}
			}

			if (defaultVal != null) {
				if (isPassword)
					return decrypt(defaultVal);
				return defaultVal;
			}

			throw new IllegalArgumentException("Missing required argument: " + name);
		}

		public boolean wasProvided() {
			if (!wasProvided) {
				for (int i = 0; i < runArgs.length; i++) {
					if (runArgs[i].equals(name)) {
						if (!singleton) {
							if (i + 1 < runArgs.length)
								this.wasProvided = true;
							else if (defaultVal != null)
								this.wasProvided = true;
							else
								throw new IllegalArgumentException("Expected a value for argument: " + name);
						} else
							wasProvided = true;
					}
				}

			}
			if (!wasProvided)
				wasProvided = defaultVal != null;
			return wasProvided;
		}

		public int getIntValue() {
			return Integer.parseInt(getValue());
		}

		public void setDefaultValue(String defaultVal) {
			this.defaultVal=defaultVal;
		}
	}

	protected String[] runArgs;
	protected List<Argument> args = new ArrayList<Argument>();
	protected final Logger log = Logger.getLogger(this.getClass());
	protected final Argument encryptArg;
	protected final Argument helpArg;
	protected long startTime;

	public Application() {
		super();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				onShutdown();
			}
		});
		this.helpArg = addArg("--help", "Show this help and exit.");
		this.helpArg.setIsSingleton();
		this.encryptArg = addArg("--encrypt",
				"Encrypt the given string and exit.  Used to encrypt passwords subsequently used by app.");
	}

	/**
	 * Called on vm shutdown.
	 * @param killed true if vm was killed (ie ctrl-c) rather than exiting gracefully.
	 */
	protected void onShutdown() {
		// override
	}

	public final void run(String[] args) {
		if (args.length == 0 && !this.args.isEmpty())
			log.info("Running without arguments, specify --help to show help.");
		this.runArgs = args;
		try {

			loadLogging();
			this.startTime=System.currentTimeMillis();
			int exit = doPrivateRun();
			if (exit != 0)
				log.info("Exit with: " + exit);
			System.exit(exit);
		} catch (IllegalArgumentException e) {
			log.error(e.getMessage(), e);
			showHelp();
		} catch (Throwable t) {
			log.error("Error during run", t);
			t.printStackTrace();
			System.exit(-1);
		}
	}

	private void loadLogging() {
		/* by default, loads from first log4j.xml or log4j.properties in classpath
		InputStream logConfigStream = null;
		try {
			String configSrc = null;
			File logFile = new File("log4j.xml");
			if (logFile.exists()) {
				logConfigStream = new BufferedInputStream(new FileInputStream(logFile));
				configSrc = logFile.getName();
			} else {
				URL configURL = this.getClass().getClassLoader().getResource("log4j.xml");
				if (configURL != null) {
					logConfigStream = configURL.openStream();
					configSrc = configURL.toString();
				}
			}
			if (logConfigStream != null) {
				Document logConfigDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(logConfigStream);
				Element logConfigE = logConfigDoc.getDocumentElement();
				DOMConfigurator.configure(logConfigE);
				log.info("Loaded logging from " + configSrc);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (logConfigStream == null) {
			BasicConfigurator.configure();
			log.warn("log4j.xml is not found in the classpath or in current directory.");
			log.info("Using default logging settings.");
		}
		*/
	}

	protected void showHelp() {
		String msg = "Usage: java " + this.getClass().getName() + " <args>\nArguments:\n";
		for (Argument arg : args) {
			msg += "\t" + arg.name + (arg.singleton ? "" : " <val>") + ": " + arg.desc
					+ (arg.defaultVal != null ? " (default: " + arg.defaultVal + ")" : "") + "\n";
		}
		//System.out.println(msg);
	}

	private int doPrivateRun() throws Exception {
		if (helpArg.wasProvided()) {
			showHelp();
			return 0;
		}
		if (encryptArg.wasProvided()) {
			//System.out.println("NOTE: this is not strong encryption and should not be relied upon as such.");
			//System.out.println("Crypted: " + encrypt(encryptArg.getValue()));
			return 0;
		}

		return doRun();
	}

	protected abstract int doRun() throws Exception;

	protected Argument addArg(String name, String desc) {
		return addArg(name, null, desc);
	}

	protected Argument addArg(String name, String defaultVal, String desc) {
		Argument arg = new Argument(name, defaultVal, desc);
		this.args.add(arg);
		return arg;
	}

	/**
	 * Convenience method to encrypt a given password.
	 * NOTE: this is not strong encryption, and should not be relied upon
	 * as such.
	 *
	 * @param pass
	 * @return
	 */
	private String encrypt(String pass) {
		// Base64 util used as base64 enc not in 1.5 vm (only 1.6)
		return Base64.encodeToString(pass.getBytes(), false);
	}

	/**
	 * Convenience method to decrypt a given password.
	 *
	 * @param encPass
	 * @return
	 * @throws DfException
	 */
	private String decrypt(String encPass) {
		// Base64 util used as base64 enc not in 1.5 vm (only 1.6)
		return new String(Base64.decodeFast(encPass));
	}
}
