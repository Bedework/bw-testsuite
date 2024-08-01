/* ********************************************************************
    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.
 */
package org.bedework.testsuite.webtest;

import org.bedework.testsuite.webtest.personalevents.AllPersonalEventTests;
import org.bedework.testsuite.webtest.publiceventsadministration.AllPubEventTests;
import org.bedework.util.args.Args;
import org.bedework.util.logging.BwLogger;
import org.bedework.util.logging.Logged;

import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import static org.junit.runner.JUnitCore.runClasses;

/** Run selenium tests.
 *
 * @author douglm
 */
public class RunTests implements Logged {
  private boolean debug = false;

	void process() {

	  final Result result = runClasses(AllPubEventTests.class,
                                     AllPersonalEventTests.class);
    for (final Failure failure: result.getFailures()) {
      info(failure.toString());
    }

	}

	boolean processArgs(final Args args) throws Throwable {
		if (args == null) {
			return true;
		}

		while (args.more()) {
			if (args.ifMatch("")) {
				continue;
			}

			if (args.ifMatch("-debug")) {
				debug = true;
			} else if (args.ifMatch("-ndebug")) {
				debug = false;
/*			} else if (args.ifMatch("-f")) {
				infileName = args.next();
				fileInput = true;
      } else if (args.ifMatch("-url")) {
        pstate.url = args.next();*/
			} else {
				error("Illegal argument: " + args.current());
				usage();
				return false;
			}
		}

		return true;
	}

	void usage() {
		System.out.println("Usage:");
		System.out.println("args   -debug");
		System.out.println("       -ndebug");
//		System.out.println("       -f <filename>");
	//	System.out.println("            specify file containing commands");
//    System.out.println("       -url <path>");
  //  System.out.println("            the service");
		System.out.println();
	}

	/** Main
	 *
	 * @param args for runtime
	 */
	public static void main(final String[] args) {
	  final RunTests rt = new RunTests();

		try {
			if (!rt.processArgs(new Args(args))) {
				return;
			}

			rt.process();
		} catch (final Throwable t) {
			rt.error(t);
		}
	}

	/* ====================================================================
	 *                   Logged methods
	 * ==================================================================== */

	private final BwLogger logger = new BwLogger();

	@Override
	public BwLogger getLogger() {
		if ((logger.getLoggedClass() == null) && (logger.getLoggedName() == null)) {
			logger.setLoggedClass(getClass());
		}

		return logger;
	}
}
