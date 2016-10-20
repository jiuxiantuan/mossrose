package com.jiuxian.mossrose.config;

/**
 * Parser for mossrose config
 * 
 * @author <a href="mailto:wangyuxuan@jiuxian.com">Yuxuan Wang</a>
 *
 */
public interface ConfigParser {

	MossroseConfig fromClasspathFile(String file);

}
