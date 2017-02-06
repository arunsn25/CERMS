package com.cosd.greenbuild.calwin.dctm.utils;

import com.cosd.greenbuild.calwin.utils.MultiThreadedApp;
import com.documentum.fc.client.DfClient;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLoginInfo;

/**
 * An application that uses processor threads to get things done. A given number
 * of processor threads are created and added to the idle queue. Each thread is
 * then taken and assigned a task and added to the busy queue. When the
 * processing finishes, the processor removes itself from the busy queue,
 * inserts itself into the idle queue and notifies of its availability. The
 * newly idle thread is then brought back into use.
 * <p/>
 * Descendants must implement behaviour to create processors and start
 * processing.
 *
 * @author Andy.Taylor
 *
 */
public abstract class MultiThreadedDFCApp extends MultiThreadedApp {

	static int ids = 1;


	protected final Argument docbaseArg;
	protected final Argument userArg;
	private final Argument passwordArg;
	protected IDfSession session;
	IDfSessionManager sMgr = null;

	public MultiThreadedDFCApp() {
		super();
		this.docbaseArg = addArg("--docbase", "ERMSDev01", "Docbase to connect to");
		this.userArg = addArg("--user", "dmadmin", "User to connect as");
		this.passwordArg = addArg("--password", "ZGN0bTEyMw==", "Crypted password for user.  Generate crypt by calling --encrypt");
	}


	@Override
	protected int doRun() throws Exception {
		try {
			this.session = createSession();
			return doRun(session);
		} finally {
			if (session != null)
				sMgr.release(session);

		}
	}

	protected int doRun(IDfSession session) throws Exception {
		return super.doRun();
	}


	/**
	 * Creates a new session, closing existing session if it exists.
	 *
	 * @return
	 * @throws DfException
	 */
	protected IDfSession createSession() throws DfException {
		if (session != null) {
			sMgr.release(session);
		}
		IDfClient client = DfClient.getLocalClient();
		sMgr = client.newSessionManager();

		DfLoginInfo loginInfo = new DfLoginInfo(userArg.getValue(), passwordArg.getValue());
		sMgr.setIdentity(docbaseArg.getValue(), loginInfo);
		//System.out.print("Connecting to: " + userArg.getValue() + "@" + docbaseArg.getValue() + "...");
		System.out.flush();
		this.session = sMgr.getSession(docbaseArg.getValue());
		//System.out.println(" connnected.");

		return session;
	}
}
