package org.petermfc.exception;

import org.apache.xmlrpc.XmlRpcException;

public class SubtitleException extends Throwable {
    public SubtitleException(XmlRpcException e) {
        super(e);
    }
}
