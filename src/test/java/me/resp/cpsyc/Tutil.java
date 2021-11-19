package me.resp.cpsyc;

import java.net.MalformedURLException;

public class Tutil {

  public static FaunaUtil faunaUtil(String collectionName) throws MalformedURLException {
    return new FaunaUtil(
        null,
        collectionName,
        JasyptUtil.decrypt(
            "ENC(QpuKlwqx2Mwwe+jSWdqUR3SXNxCF+8ERYpXkzD9+gvXGLJIw6wQCoelk+14ItLc+LudmM7qDV00SpMRjAAl1sMCZk5lv7DEyZuzDCZ4IdD0=)"),
        JasyptUtil.decrypt(
            "ENC(vTf4BXEMTEQwlePoLpdRinJTG3qkNDyg7YdDn8+irmEeK2rwAb2qFO1irvJuZBc0KCCrNZDkB8aY6z3BXC6ZtVBmmhDn+QXg5rPs2vl6j8s=)"));
  }

  public static String serverKey() {
    return JasyptUtil.decrypt(
        "ENC(vTf4BXEMTEQwlePoLpdRinJTG3qkNDyg7YdDn8+irmEeK2rwAb2qFO1irvJuZBc0KCCrNZDkB8aY6z3BXC6ZtVBmmhDn+QXg5rPs2vl6j8s=)");
  }

  public static String adminKey() {
    return JasyptUtil.decrypt(
        "ENC(QpuKlwqx2Mwwe+jSWdqUR3SXNxCF+8ERYpXkzD9+gvXGLJIw6wQCoelk+14ItLc+LudmM7qDV00SpMRjAAl1sMCZk5lv7DEyZuzDCZ4IdD0=)");
  }

  public static String basicHeader() {
    return JasyptUtil.decrypt(
        "ENC(PUEEtE4c3OzQEt2QOv46WvCQpQRSKNcP2b8DcbGDJyPOHMtiqRJXWy4JcP9i8P8/h1P9rVcuzh+08YNh0HZrgOq/Sekd13DORUlzie/k4NFC0DvyxhZ3HaiJ4WT5J1Vs2vXIb9XNKAY+WNUQwPIn9A==)");
  }
}
