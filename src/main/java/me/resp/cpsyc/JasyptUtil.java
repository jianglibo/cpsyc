package me.resp.cpsyc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

public class JasyptUtil {
  protected static Pattern pattern = Pattern.compile("ENC\\(([^)]+)\\)");

  // https://github.com/ulisesbocchio/jasypt-spring-boot
  // https://github.com/ulisesbocchio/jasypt-spring-boot/blob/75dad8da4dd3fd904e7f245f9ac7b584eda9f436/jasypt-spring-boot/src/main/java/com/ulisesbocchio/jasyptspringboot/configuration/StringEncryptorBuilder.java
  public static StringEncryptor stringEncryptor(String password) {
    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    
    config.setPassword(password);
    config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
    config.setKeyObtentionIterations("1000");
    config.setPoolSize("1");
//     config.setProviderName("SunJCE");
    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
    config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
    config.setStringOutputType("base64");
    encryptor.setConfig(config);
    return encryptor;
  }

  public static StringEncryptor stringEncryptor() {
	  String p = System.getenv("CPSYC_JASYPT_PASSWORD");
    return stringEncryptor(p);
  }

  public static String encrypt(String toEncrypt) {
    return String.format("ENC(%s)", stringEncryptor().encrypt(toEncrypt));
  }

  public static String decrypt(String toDecrypt) {
    Matcher matcher = pattern.matcher(toDecrypt);
    if (matcher.matches()) {
      return stringEncryptor().decrypt(matcher.group(1));
    } else {
      return stringEncryptor().decrypt(toDecrypt);
    }
  }
}
