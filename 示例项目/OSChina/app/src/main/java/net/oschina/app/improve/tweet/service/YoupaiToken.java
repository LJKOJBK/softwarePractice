package net.oschina.app.improve.tweet.service;


import java.io.Serializable;

 class YoupaiToken implements Serializable{
    private String secret;
    private String operator;

     public String getSecret() {
         return secret;
     }

     public void setSecret(String secret) {
         this.secret = secret;
     }

     public String getOperator() {
         return operator;
     }

     public void setOperator(String operator) {
         this.operator = operator;
     }
 }
