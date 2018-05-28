/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redhat.embedded;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.AprLifecycleListener;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.coyote.http2.Http2Protocol;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;

import javax.servlet.ServletException;

public class EmbeddedTomcatEx {

    public static void main(String[] args) throws LifecycleException, ServletException {

        Tomcat tomcat = new Tomcat();
        Service service = tomcat.getService();
        //Create SSL connector
        service.addConnector(EmbeddedTomcatEx.getSslConnector());
        service.addLifecycleListener(getAprListener());
        tomcat.setBaseDir(".");
        tomcat.start();
        tomcat.getServer().await();
    }
    
    private static Connector getSslConnector() {
        Connector con = new Connector();
        con.setPort(8443);
        
        Http11NioProtocol protocol = (Http11NioProtocol) con.getProtocolHandler();
        protocol.setSSLEnabled(true);
        protocol.setMaxThreads(150);
        
        Http2Protocol http2 = new Http2Protocol();
        con.addUpgradeProtocol(http2);
        
        SSLHostConfig sslConf = new SSLHostConfig();
        sslConf.setTruststoreFile("../conf/cacerts");
        sslConf.setTruststorePassword("");
        sslConf.setCertificateVerification("optional");

        SSLHostConfigCertificate cert = new SSLHostConfigCertificate(sslConf, SSLHostConfigCertificate.Type.RSA);
        cert.setCertificateKeystoreFile("../conf/tomcat_keystore.jks");
        cert.setCertificateKeystorePassword("tomcat");
        sslConf.addCertificate(cert);
        
        con.addSslHostConfig(sslConf);
        
        return con;
    }

    private static LifecycleListener getAprListener(){
        AprLifecycleListener apr = new AprLifecycleListener();
        apr.setUseOpenSSL(true);

        return apr;
    }
}
