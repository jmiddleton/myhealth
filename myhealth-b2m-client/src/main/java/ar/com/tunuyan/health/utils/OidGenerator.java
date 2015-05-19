package ar.com.tunuyan.health.utils;

import java.math.BigInteger;
import java.util.UUID;

/**
 * OID identifier
 * 
 * @author jmiddleton
 *
 */
public class OidGenerator {
	private static final String SUFFIX = "2.25.";

	private static String convertUUIDtoOID(String uuid) {
		String strippedUUID = uuid.replaceAll("-", "");
		strippedUUID = strippedUUID.toLowerCase();
		String oid = SUFFIX + getWeightedSum(strippedUUID);
		return oid;
	}

	private static BigInteger getWeightedSum(String str) {
		BigInteger bigInt = new BigInteger(str, 16);
		return bigInt;
	}

	public static String genOID() {
		return convertUUIDtoOID(UUID.randomUUID().toString());
	}

}
