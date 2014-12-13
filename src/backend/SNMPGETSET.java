/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.SimpleVariableTextFormat;
/**
 *
 * @author Joshua
 */
public class SNMPGETSET {
    public static final int SNMP_PORT = 161;
    public static final String COMMUNITY_PUBLIC = "public";
    public static final String COMMUNITY_CREATOR = "creator";
    public static final String COMMUNITY_WRITER = "writer";
    public static final int mSNMPVersion = 2; // 0 represents SNMP version=1
    public static final String OID_UPS_OUTLET_GROUP = ".1.3.6.1.4.1.318.1.1.1.12.3.2.1.3.1";
    public static final String OID_UPS_BATTERY_CAPACITY = ".1.3.6.1.4.1.318.1.1.1.2.2.1.0";
    
    //iso.org.dod.internet.mgmt.mib-2.system.sysUpTime
    public static final String upTime = "1.3.6.1.2.1.1.3.0";
    public static final String contactName = ".1.3.6.1.2.1.1.5.0";
    public static final String location = ".1.3.6.1.2.1.1.6.0";
    
    
    public static final String iPForwarding = "1.3.6.1.2.1.4.1.0";
    
    private int timeOut = 2000;
    private int retryAmount = 2;
    
    
    private static InetAddress serverAddress;

    /*
    public static void main(String args[]) throws UnknownHostException, IOException, ParseException
    {
        System.out.println("JoshVersion");
        
        int valueToSend = 4;
        
        SNMPGETSET snmpObj = new SNMPGETSET(InetAddress.getByName("192.168.88.131"));
        
        snmpObj.snmpSet(serverAddress, COMMUNITY_WRITER, location, valueToSend);
        snmpObj.snmpSet(serverAddress, COMMUNITY_WRITER, iPForwarding, valueToSend);
        
        snmpObj.snmpSet(serverAddress, COMMUNITY_WRITER, contactName, "HelloPeople");
        snmpObj.snmpSet(serverAddress, COMMUNITY_WRITER, iPForwarding, 1);
        
        //snmpObj.snmpGet(serverAddress, COMMUNITY_PUBLIC, OID_UPS_BATTERY_CAPACITY);
        System.out.println("Up Time = "+ snmpObj.snmpGet(serverAddress, COMMUNITY_PUBLIC, upTime));
        System.out.println("Contact Name = "+ snmpObj.snmpGet(serverAddress, COMMUNITY_PUBLIC, contactName));
        
        System.out.println("iPForwarding = "+ snmpObj.snmpGet(serverAddress, COMMUNITY_PUBLIC, iPForwarding));
    }
    */
    public SNMPGETSET(){}
    
    public SNMPGETSET(InetAddress servAddress)
    {
        serverAddress = servAddress;
    }
    
    public void setSendAddress(InetAddress servAddress)
    {
        serverAddress = servAddress;
    }
    
    public void snmpSet(InetAddress serverIPAdd, String commun, String strOID, int Value) throws IOException
    {
        OctetString community = new OctetString(commun);
        
        Address targetAddress = new UdpAddress(serverIPAdd, SNMP_PORT);
        
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();
        
        Snmp snmp = new Snmp(transport);
        
        CommunityTarget target = new CommunityTarget(targetAddress, community);
        target.setVersion(SnmpConstants.version2c);
        target.setRetries(retryAmount);
        target.setTimeout(timeOut);
        
        Variable var = new Integer32(Value);
        
        PDU pdu = new PDU();
        pdu.setType(PDU.SET);
        pdu.add(new VariableBinding(new OID(strOID), var));
        
        
        ResponseListener listener = new ResponseListener() 
        {
            @Override
            public void onResponse(ResponseEvent event) 
            {
                ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                System.out.println("Set Status is: " + event.getResponse().getErrorStatusText());
            }
        };
        
        snmp.send(pdu, target, null, listener);
        snmp.close();   
    }
    
    public void snmpSet(InetAddress serverIPAdd, String commun, String strOID, String Value) throws IOException
    {
        OctetString community = new OctetString(commun);
        
        Address targetAddress = new UdpAddress(serverIPAdd, SNMP_PORT);
        
        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();
        
        Snmp snmp = new Snmp(transport);
        
        CommunityTarget target = new CommunityTarget(targetAddress, community);
        target.setVersion(SnmpConstants.version2c);
        target.setRetries(retryAmount);
        target.setTimeout(timeOut);
        
        Variable var = new OctetString(Value);
        
        PDU pdu = new PDU();
        pdu.setType(PDU.SET);
        pdu.add(new VariableBinding(new OID(strOID),var));
        
        
        ResponseListener listener = new ResponseListener() 
        {
            @Override
            public void onResponse(ResponseEvent event) 
            {
                ((Snmp) event.getSource()).cancel(event.getRequest(), this);
                System.out.println("Set Status is: " + event.getResponse().getErrorStatusText());
            }
        };
        
        snmp.send(pdu, target, null, listener);
        snmp.close();   
    }
    
    public InetAddress getAddress()
    {
        return serverAddress;
    }
    
    public String snmpGet(InetAddress serverIPAdd, String commun, String strOID) throws IOException 
    {
        String response = "";
        OctetString community = new OctetString(commun);
         
        Address targetAddress = new UdpAddress(serverIPAdd, SNMP_PORT);

        TransportMapping transport = new DefaultUdpTransportMapping();
        transport.listen();
        
        Snmp snmp = new Snmp(transport);
        
        CommunityTarget target = new CommunityTarget(targetAddress, community);
        target.setVersion(SnmpConstants.version2c);
        target.setRetries(retryAmount);
        target.setTimeout(timeOut);
        
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(strOID)));
        
        ResponseEvent responseEvent = snmp.get(pdu, target);
        
        response = responseEvent.getResponse().getVariableBindings().firstElement().toString();
        
        snmp.close();
        //System.out.println(response);
        return response;
     }
}
