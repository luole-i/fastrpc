package cn.qenan.fastrpc.client.failhand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FailFast {
    private final static Logger LOGGER = LoggerFactory.getLogger(FailFast.class);

    public static void execute(Exception e){
        LOGGER.error(e.getMessage());
    }
}
