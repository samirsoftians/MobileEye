package com.twtech.fleetviewapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Created by twteh on 10/2/18.
 */

public class IncidentDataTransmissionNewLogic implements Runnable {

    String getId;
    String hostpop3, portpop3, userNamepop3, passwordpop3, subpop3;
    Context Context;
    String SNo, TableName;
    Thread CurrThread;
    String RegularDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDRegularStamp.db";
    String ExceptionDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDExceptionStamp.db";
    String IncidentDatabase = Environment.getExternalStorageDirectory().getPath() + "/DDIncidentData.db";
    String UnitID, Password, SmtpHost, SmtpPort, ToMailId, IncidentMailLength;
    int SleepInterval;
    String file = Environment.getExternalStorageDirectory().getPath() + "/IncidentData.txt";
    String Tag="IncidentDataTransmissionNewLogic";

    public IncidentDataTransmissionNewLogic(Context mContext) {
        this.Context = mContext;

       // new MyLogger().storeMassage("IncidentDataTransmissionOldLogic", "Called");

        try {
            CanDatabase canDatabase = new CanDatabase(mContext);
            canDatabase.openCanDatabase();
            UnitID = canDatabase.getValue("UnitID");
            hostpop3 = canDatabase.getValue("POP3HostRoute");
            portpop3 = canDatabase.getValue("POP3PortRoute");
            userNamepop3 = canDatabase.getValue("POP3UserNameRoute");
            passwordpop3 = canDatabase.getValue("POP3PasswordRoute");

            //UnitID = canDatabase.getValue("UnitID");
            Password = canDatabase.getValue("SmtpPassword");
            SmtpHost = canDatabase.getValue("SmtpHost");
            SmtpPort = canDatabase.getValue("SmtpPort");
            ToMailId = canDatabase.getValue("ToMailIdIncident");
            IncidentMailLength = canDatabase.getValue("IncidentMailLength");

            canDatabase.closeCanDatabase();

            SleepInterval = Integer.parseInt(IncidentMailLength);
           // new MyLogger().storeMassage("IncidentDataTransmissionOldLogic Retrieve POP3 Data from CAN Database", "UnitId-" + UnitID + ", Password-" + passwordpop3 + ", UserNamepop3-" + userNamepop3 + ", POP3Host-" + hostpop3 + ", POP3Port-" + portpop3 + ", ToMailId-" + ToMailId);
           // new MyLogger().storeMassage("IncidentDataTransmissionOldLogic Retrieve SMTP Data from CAN Database", "UnitId-" + UnitID + ", Password-" + Password + ", SMTPHost-" + SmtpHost + ", SMTPPort-" + SmtpPort + ", ToMailId-" + ToMailId + ", SleepInterval-" + SleepInterval);
            //new DatabaseOperations(mContext).storeRegularLog("1", "ExceptionStampsTransmission method called Retrieve Data" + "unitId-" + unitID + "password-" + password + "smtpPort-" + smtpPort +  "toMailId-" + toMailId);

            CurrThread = new Thread(this);
            CurrThread.start();

        } catch (Exception e) {
            new MyLogger().storeMassage(Tag+" : Exception while getValue from CanDatabase ", "in IncidentStampsTransmission");
            //databaseOperations.insertData(exceptionLogDatabase, Tag + "Exception while getValue from CanDatabase");
            //new DatabaseOperations(mContext).storeExceptionLog("3", "Exception while getValue from CanDatabase");
        }
    }

    @Override
    public void run() {

        while (true)
            try {
               // new MyLogger().storeMassage("Run method of IncidentStampsTransmission", "Called");
                new MailAction().execute(hostpop3, portpop3, userNamepop3, passwordpop3, subpop3);
               // updateStatusNull(IncidentDatabase);

                Thread.sleep(10 * 60* 1000);
                //CurrThread.sleep(10*60*1000);

            } catch (Exception e) {
                e.printStackTrace();
                new MyLogger().storeMassage(Tag," Exception while IncidentDataTransmissionOldLogic run method");
                //new DatabaseOperations(mContext).storeExceptionLog("3", "Exception in TransmissionMailRunnable run method");
            }
    }

    private class MailAction extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
           // new MyLogger().storeMassage("Mail Action : ", " Called");

            subpop3 = UnitID + "#ACCD";
           // new MyLogger().storeMassage(Tag+" : Main Action ACCD, Subject should be", " : " + subpop3);
          //  new MyLogger().storeMassage("MailAction Asynctask  : ", " Called");
           // new MyLogger().storeMassage("host", " : " + hostpop3);
           // new MyLogger().storeMassage("port", " : " + portpop3);
          //  new MyLogger().storeMassage("userName", " : " + userNamepop3);
          //  new MyLogger().storeMassage("password", " : " + passwordpop3);
          //  new MyLogger().storeMassage("sub", " : " + subpop3);

            Boolean f = emailAttachmentReceive(hostpop3, portpop3, userNamepop3, passwordpop3, subpop3);
          //  new MyLogger().storeMassage("f value", String.valueOf(f));

          //  new MyLogger().storeMassage("OnPostExecution", "Called");

            return f;
        }
    }

    public Boolean emailAttachmentReceive(String hostP, String portP, final String userNameP, final String passwordP, String subP) {
      //  new MyLogger().storeMassage(Tag+" : emailAttachmentReceive", " Called");
        boolean found = false;
        boolean flag;
        //String attachFiles = "";
        // new MyLogger().storeMassage("attachFiles", "" + attachFiles);
        Properties properties = new Properties();
        // server setting
        properties.put("mail.pop3.host", hostP);
        properties.put("mail.pop3.port", portP);
        properties.put("mail.smtp.auth", "true");
       // new MyLogger().storeMassage("mail.pop3.host", "log");

        // SSL setting
        properties.setProperty("mail.pop3.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.pop3.socketFactory.fallback", "false");
        properties.setProperty("mail.pop3.socketFactory.port",
                String.valueOf(portP));

      //  new MyLogger().storeMassage("SSL Setting ", "log");

        // Session session = Session.getInstance(properties);
        //Session session = Session.getDefaultInstance(properties);
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userNameP, passwordP); // username and the password
            }
        });

      //  new MyLogger().storeMassage("session ", "created");

        try {
           // new MyLogger().storeMassage("entered into..................", "try");
            // connects to the message store
            Store store = session.getStore("pop3");
           // new MyLogger().storeMassage("Store.................", "Called");
            store.connect(userNameP, passwordP);
          //  new MyLogger().storeMassage("UserName................", userNameP);
           // new MyLogger().storeMassage("Password.................", passwordP);

            // opens the inbox folder
            Folder folderInbox = store.getFolder("INBOX");
          //  new MyLogger().storeMassage("Inbox................", String.valueOf(folderInbox));
            // folderInbox.open(Folder.READ_ONLY);
            folderInbox.open(Folder.READ_WRITE);

            // fetches new messages from server
            javax.mail.Message[] arrayMessages = folderInbox.getMessages();

            for (int i = (arrayMessages.length - 1); i >= 0; i--) {
                javax.mail.Message message = arrayMessages[i];
                Address[] fromAddress = message.getFrom();
                String from = fromAddress[0].toString();
               // new MyLogger().storeMassage("fromaddress", " : " + fromAddress);

                String subject = message.getSubject();

                String sentDate = message.getSentDate().toString();

                if (subject.equals(subP)) {

                    // print out details of each message
                    System.out.println("Message #" + (i + 1) + ":");
                    System.out.println("\t From: " + from);
                    System.out.println("\t Subject: " + subject);
                    System.out.println("\t Sent Date: " + sentDate);
                    //System.out.println("\t Message: " + messageContent);
                    //System.out.println("\t Attachments: " + attachFiles);

                   // new MyLogger().storeMassage("Message", " : " + (i + 1));
                   // new MyLogger().storeMassage("From", " : " + from);
                  //  new MyLogger().storeMassage("Subject", " : " + subject);
                  //  new MyLogger().storeMassage("sentDate", "" + sentDate);
                    //new MyLogger().storeMassage("Attachments", " : " + attachFiles);
                    //code to delete email....
                    try {
                       // new MyLogger().storeMassage("Mail found", "Successfully");

                        if (true) {

                            try {

                                TWsimpleMailSender mTWsimpleMailSender = new TWsimpleMailSender(Context,UnitID, Password, SmtpHost, SmtpPort);
                              //  new MyLogger().storeMassage("MailSender ", "Called.............");
                                // new DatabaseOperations(mContext).storeRegularLog("1", "MailSender Called");
                                //boolean flag = mTWsimpleMailSender.sendMail(unitID, "Hello", unitID, "s_nirhali@twtech.in", IncidentDatabase, canDatabase);
                              // mTWsimpleMailSender.sendMail(UnitID, "", UnitID, "d_shinde@transworld-compressor.com", IncidentDatabase);
                                mTWsimpleMailSender.sendMail(UnitID, "", UnitID, ToMailId, IncidentDatabase);
                              //  new MyLogger().storeMassage("MailSend ", "Successfully.................");

                            } catch (Exception e) {
                                e.printStackTrace();
                                new MyLogger().storeMassage(Tag+" : Exception in Transmission", e.getMessage());

                            }

                        }
                            arrayMessages[i].setFlag(Flags.Flag.DELETED, true);

                       // new MyLogger().storeMassage(Tag+" : Email ", "Deleted !!!!" + arrayMessages[i]);
                    } catch (Exception e) {
                       // new MyLogger().storeMassage(Tag+" : Email ", "Deleted !!!!" + e.getMessage());
                    }

                    break;
                }
            }
            // disconnect
            folderInbox.close(true);
            store.close();
            // Log.i("***EmailAttachment***", "Completed");
            //new MyLogger().storeMassage("***EmailAttachment***", "Completed");
        } catch (javax.mail.NoSuchProviderException ex) {
            System.out.println("No provider for pop3.");
            new MyLogger().storeMassage(Tag+" : NoSuchProviderException", " : " + ex.getMessage());
            ex.printStackTrace();
        } catch (MessagingException ex) {
            System.out.println("Could not connect to the message store");
            new MyLogger().storeMassage(Tag+" : MessagingException", " :  " + ex.getMessage());
            ex.printStackTrace();
        }
        //new MyLogger().storeMassage(Tag+" : found value", String.valueOf(found));
        return found;
    }
}
