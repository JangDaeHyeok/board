package com.example.demo.util;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UUIDUtil {

	private static final Logger logger = LoggerFactory.getLogger(UUIDUtil.class);
	
	/**
	 * @Developer 이강민
	 * @Description UUID 생성시 "-" 제거
	 */
	public static String createUUID() {
		String uuid = UUID.randomUUID().toString().replace("-","");
		logger.info("[UUID생성] 생성된 UUID : " + uuid);
		return uuid;
	}
	
	/**
	 * @Developer 이강민
	 * @Description UUID 생성시 "-" 제거 및 16자리 숫자로 Cut
	 */
	public static String createUUID16() {
		String uuid = UUID.randomUUID().toString().replace("-","").substring(0, 16).toUpperCase();
		logger.info("[UUID생성] 생성된 UUID(16자리) : " + uuid);
		return uuid;
	}
}
