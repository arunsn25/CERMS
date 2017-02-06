package com.cosd.greenbuild.calwin.server;

import java.io.PrintWriter;
import java.util.Map;


import com.documentum.fc.client.IDfBusinessObject;
import com.documentum.fc.client.IDfModule;
import com.documentum.fc.methodserver.IDfMethod;

public class CalwinServerMethodJMS implements IDfMethod, IDfModule, IDfBusinessObject
{

	 @Override
	 public int execute(Map arg0, PrintWriter arg1) throws Exception {
	  return 0;
	 }

	 @Override
	 public String getVendorString() {
	  return  "";
	 }

	 @Override
	 public String getVersion() {
	  return "1.0";
	 }

	 @Override
	 public boolean isCompatible(String arg0) {
	  return true;
	 }

	 @Override
	 public boolean supportsFeature(String arg0) {
	  return true;
	 }
}