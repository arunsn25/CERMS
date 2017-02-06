package com.cosd.greenbuild.calwin.mashup;

import com.cosd.greenbuild.calwin.utils.COSDCalwinConstants;

/**
 * Thrown when the maximum total length has been exceeded for a mashup.
 * Maximum total length is the combined length of documents in a mashup, 
 * and is configured in the mashup configuration object (currently the
 * /CDCR-Config/Config/erms_adobe_asembler_ws object)
 * 
 * @see CDCRConstants#GET_ADOBE_ASEMBLER_CONFIG_QRY
 * @author Andy.Taylor
 *
 */
public class MaxTotalLengthExceededException extends Exception {

	private static final long serialVersionUID = 1L;

	public MaxTotalLengthExceededException(String msg) {
		super(msg);
	}

}
