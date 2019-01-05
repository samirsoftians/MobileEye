package com.twtech.fleetviewapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;

/**
 * Created by Deepali Shinde on 11/5/18.
 */

public class UpdateCanParameter implements Runnable{

    Context mCtxx;
    CanDatabase canDatabase;
    String hostpop3, portpop3, userNamepop3, passwordpop3, subpop3, canUpdateSleepTime,unitID;
    String parameter, value, Attachment ;
    String databaseName = Environment.getExternalStorageDirectory().getPath() + "/DDCanDatabase.db";
    int sleepInterval = 3600;
    Thread thisThread;
    String Tag="UpdateCanParameter";

    public UpdateCanParameter(Context mCtxx) {
        this.mCtxx = mCtxx;
       // new MyLogger().storeMassage("UpdatRemoteCanParameter service", "called");
        canDatabase = new CanDatabase(mCtxx);
        canDatabase.openCanDatabase();
        unitID = canDatabase.getValue("UnitID");
        canUpdateSleepTime= canDatabase.getValue("CanUpdateSleepTime");
        hostpop3 = canDatabase.getValue("POP3HostRoute");
        portpop3 = canDatabase.getValue("POP3PortRoute");
        userNamepop3 = canDatabase.getValue("POP3UserNameRoute");
        passwordpop3 = canDatabase.getValue("POP3PasswordRoute");
        subpop3 = canDatabase.getValue("POP3Subject");
        canDatabase.closeCanDatabase();
        sleepInterval = Integer.parseInt(canUpdateSleepTime);
        thisThread = new Thread(this);
        thisThread.start();
    }


    @Override
    public void run() {
        try {
            while(true) {
               // new MyLogger().storeMassage(Tag, " Called");
                //new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("1", "Thread Start for UpdateRemoteParameter Called");

                new MailAction().execute(hostpop3, portpop3, userNamepop3, passwordpop3, subpop3);
                //new MyLogger().storeMassage("Can Update Sleep Time "," : "+sleepInterval);

                try {
                    thisThread.sleep(sleepInterval*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // new MyLogger().storeMassage("Thread called.............", "SuccessfullySS");
                //sleep(sleepInterval*1000);

            }
        } catch (Exception e) {
            e.printStackTrace();
            new MyLogger().storeMassage(Tag," Exception while start thread ");
            // new DatabaseOperations(UpdateRemoteCanParameter.this).storeExceptionLog("1", "Exception while start thread for UpdateRemoteParameter method");
        }
    }

    private class MailAction extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
           // new MyLogger().storeMassage(Tag+" : Mail Action : ", " Called");
            // new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("1", "MainAction Called");

            subpop3 = unitID + "CAN_UPDATE";
            new MyLogger().storeMassage(Tag+" : Can Update Subject "," : "+subpop3);
          //  new MyLogger().storeMassage("Can Value of",", pop3host-" + hostpop3 + ", pop3port-" + portpop3 + ", pop3userName-" + userNamepop3 + ", pop3password-" + passwordpop3 + ", pop3sbject-" + subpop3 + ", sleepInterval-" + sleepInterval );

            Boolean f = emailAttachmentReceive(hostpop3, portpop3, userNamepop3, passwordpop3, subpop3);
            // return f;
          //  new MyLogger().storeMassage("OnPostExecution", "Called");
            //new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("2", "OnPostExecution Called");

            return f;
        }
    }

    public Boolean emailAttachmentReceive(String hostP, String portP, String userNameP, String passwordP, String subP) {
       // new MyLogger().storeMassage(Tag+" : emailAttachmentReceive", " : Called");
        // new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("1", "emailAttachmentReceive Called of UpdaterRemoteParameter");
        boolean found = false;
        String attachFiles = "";
        //new MyLogger().storeMassage("attachFiles", "" + attachFiles);
        Properties properties = new Properties();

        // server setting
        properties.put("mail.pop3.host", hostP);
        properties.put("mail.pop3.port", portP);
        properties.put("mail.smtp.auth", "true");
       // new MyLogger().storeMassage("mail.pop3.host", "log");
        //new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("2", "mail.pop3.host log");

        // SSL setting
        properties.setProperty("mail.pop3.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.pop3.socketFactory.fallback", "false");
        properties.setProperty("mail.pop3.socketFactory.port",
                String.valueOf(portP));

      //  new MyLogger().storeMassage("SSL Setting ", "log");
        //new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("2", "SSL Setting log");

        // Session session = Session.getInstance(properties);
        //Session session = Session.getDefaultInstance(properties);
        Session session= Session.getDefaultInstance(properties,new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("jrmroute@mobile-eye.in","jrmroute@123"); // username and the password
            }
        });

      //  new MyLogger().storeMassage("session ", "created");

        //new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("2", "Session created");
        try {

            //  new MyLogger().storeMassage("entered into..................", "try");
            // connects to the message store
            Store store = session.getStore("pop3");
           // new MyLogger().storeMassage("Store.................", "Called");
            //new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("2", "Store Called");
            store.connect(userNameP, passwordP);
           // new MyLogger().storeMassage("UserName................", userNameP);
           // new MyLogger().storeMassage("Password.................", passwordP);

            //new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("2", "UserName-" +userNameP + ", Password-" +passwordP);
            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
           // new MyLogger().storeMassage("Inbox................", String.valueOf(folderInbox));
            //new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("2", "Inbox- " + folderInbox);
            // folderInbox.open(Folder.READ_ONLY);
            folderInbox.open(Folder.READ_WRITE);

            // fetches new messages from server
            javax.mail.Message[] arrayMessages = folderInbox.getMessages();

            for (int i = (arrayMessages.length - 1); i >= 0; i--) {
                javax.mail.Message message = arrayMessages[i];
                Address[] fromAddress = message.getFrom();
                String from = fromAddress[0].toString();
              //  new MyLogger().storeMassage("fromaddress", " : " + fromAddress);
                //new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("2", "FromAddress- " +fromAddress);

                String subject = message.getSubject();

                String sentDate = message.getSentDate().toString();

                if (subject.equals(subP)) {

                    String contentType = message.getContentType();

                    if (contentType.contains("multipart")) {

                        // content may contain attachments
                        Multipart multiPart = (Multipart) message.getContent();
                        int numberOfParts = multiPart.getCount();
                        ///Log.i("***EmailAttach***","inside if");
                        for (int partCount = 0; partCount < numberOfParts; partCount++) {
                            MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                            ///Log.i("***EmailAttach***","inside for");
                            if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                                // this part is attachment
                                ///Log.i("***Emai+03lAttach***","inside inner if");
                                String fileName = part.getFileName();

                                attachFiles = fileName;

                                found = true;

                                InputStream inputStream = part.getInputStream();

                                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                                //FileOutputStream fout = openFileOutput(attachFiles, MODE_PRIVATE);
                                //LEDscheduleC = "";
                                Attachment = "";
                                //String f="";
                                int c = 0, d = 0;
                                Log.i("***EmailAttach***", "file:" + fileName);
                                //new MyLogger().storeMassage("Filename", " : " + fileName);
                                // new DatabaseOperations(UpdateRemoteParameter.this).storeRegularLog("2", "Attachment File Name of UpdateRemoteParameter of Can " +fileName);

                                String data;

                                while ((data = br.readLine()) != null) {
                                    Attachment = Attachment + data;
                                    d++;
                                }
                                Log.e("Data",": "+data);
                                Log.i("***EmailAttach***", "" + Attachment);
                                // String[] newsched = CountBackupDays.split("_");
                                String[] newAttachment = Attachment.split("%");

                                int j = newAttachment.length;

                               // new MyLogger().storeMassage("newAttachment.........", " length: " + j);
                                //new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("2", "newAttachment length" + i);
                                //code for getting parameters from text file and inserting into database

                                try {
                                    for (int p = 0; p < j; p++) {

                                        String temp = newAttachment[p];

                                        String[] parts = temp.split(",");

                                        parameter = "" + parts[0];
                                        value = "" + parts[1];

                                       // new MyLogger().storeMassage("parameter-" + parameter, "value-" + value);
                                        // new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("2", "Parameter-" +parameter + "Value-" + value);

                                        try {

                                            canDatabase.openCanDatabase();
                                            canDatabase.storeValue(parameter, value);
                                            canDatabase.closeCanDatabase();

                                            //new MyLogger().storeMassage(Tag+" : Update data in Can database", ":" +parameter + ":" +value );
                                            //new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("3", "Update data in Candatabase " +parameter + ":" +value );
                                        } catch (Exception e) {
                                            new MyLogger().storeMassage(Tag," : Exception while update value in CanDatabase");
                                            //new DatabaseOperations(UpdateRemoteCanParameter.this).storeExceptionLog("1", "Exception while update value in CanDatabase");
                                        }
                                    }
                                } catch (Exception e) {
                                    new MyLogger().storeMassage(Tag+" : Exception while ", "splitting for loop........");
                                    //new DatabaseOperations(UpdateRemoteCanParameter.this).storeExceptionLog("2", "Exception while splitting for loop");
                                }
                            }
                        }
                    }
                    // print out details of each message
                    System.out.println("Message #" + (i + 1) + ":");
                    System.out.println("\t From: " + from);
                    System.out.println("\t Subject: " + subject);
                    System.out.println("\t Sent Date: " + sentDate);
                    //System.out.println("\t Message: " + messageContent);
                    System.out.println("\t Attachments: " + attachFiles);

                   // new MyLogger().storeMassage("Message", " : " + (i + 1));
                    new MyLogger().storeMassage(Tag+ " From Address", " : " + from+" Subject : "+subject+" Attachments : "+attachFiles);
                    try {
                        arrayMessages[i].setFlag(Flags.Flag.DELETED, true);
                    } catch (Exception e) {
                        new MyLogger().storeMassage(Tag+" : Email ", "Deleted !!!!" + e.getMessage());
                        //new DatabaseOperations(UpdateRemoteCanParameter.this).storeExceptionLog("2", "Exception while email deleted");
                    }
                    break;
                }
            }

            // disconnect
            folderInbox.close(true);
            store.close();
            Log.i("***EmailAttachment***", "Completed");
           // new MyLogger().storeMassage("***EmailAttachment***", "Completed");
            //new DatabaseOperations(UpdateRemoteCanParameter.this).storeRegularLog("3", "EmailAttachemt Completed");
        } catch (javax.mail.NoSuchProviderException ex) {
            System.out.println("No provider for pop3.");
            new MyLogger().storeMassage(Tag+" : NoSuchProviderException", " : " + ex.getMessage());
            //new DatabaseOperations(UpdateRemoteCanParameter.this).storeExceptionLog("3", "NoSuchProviderException while UpdaterRemoteParameter");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            new MyLogger().storeMassage(Tag+" : MessagingException", " :  " + ex.getMessage());
            //new DatabaseOperations(UpdateRemoteCanParameter.this).storeExceptionLog("3", "MessagingException while Could not connect to the message store");
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
            new MyLogger().storeMassage(Tag+" : IOException", " : " + ex.getMessage());
            //new DatabaseOperations(UpdateRemoteCanParameter.this).storeExceptionLog("3", "IOException while UpdaterRemoteParameter method");
        }
        return found;
    }
}
