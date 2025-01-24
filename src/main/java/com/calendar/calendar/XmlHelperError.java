package com.calendar.calendar;

import java.io.Serial;

public class XmlHelperError extends Exception {
    
    @Serial
    private static final long serialVersionUID = 1L;

	public XmlHelperError(String errorMessage) {
        super(errorMessage);
    }

}
