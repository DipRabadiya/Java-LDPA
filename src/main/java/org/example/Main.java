package org.example;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.Properties;
import java.util.concurrent.TransferQueue;

public class Main {

    DirContext connection;
    public void newConnection(){
        Properties env = new Properties();
        env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL,"ldap://localhost:10389");
        env.put(Context.SECURITY_PRINCIPAL,"uid=admin,ou=system");
        env.put(Context.SECURITY_CREDENTIALS,"secret");

        try {
            connection = new InitialDirContext(env);
            System.out.println("Hello World!" + connection);
        } catch (AuthenticationException ex){
            System.out.println(ex.getMessage());
        } catch (NamingException e){
            e.printStackTrace();
        }
    }

    public void getAllUsers() throws NamingException {
        String searchFilter = "(objectClass=inetOrgPerson)";
        String[] reqAtt = {"cn","sn"};
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(reqAtt);

        NamingEnumeration users = connection.search("ou=users,ou=system",searchFilter,controls);

        SearchResult result = null;
        while (users.hasMore()){
            result = (SearchResult) users.next();
            Attributes attr = result.getAttributes();

            String name = attr.get("cn").get(0).toString();
//            addUserToGroup(name,"Administrators");

//            deleteUserFromGroup(name,"Administrators");

            System.out.println(attr.get("cn"));
            System.out.println(attr.get("sn"));
        }
    }

    public void addUser(){
        Attributes attributes = new BasicAttributes();
        Attribute attribute = new BasicAttribute("objectClass");
        attribute.add("inetOrgPerson");

        attributes.put(attribute);
        //
        attributes.put("sn","Smit");

        try {
            connection.createSubcontext("cn=vaghani,ou=users,ou=system",attributes);
            System.out.println("success");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUserToGroup(String username,String groupName){
        ModificationItem[] mods = new ModificationItem[1];
        Attribute attribute = new BasicAttribute("uniqueMember","cn="+username+",ou=users,ou=system");
        mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE,attribute);

        try {
            connection.modifyAttributes("cn="+groupName+",ou=groups,ou=system",mods);
            System.out.println("Success");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteUser(){
        try {
            connection.destroySubcontext("cn=vaghani,ou=users,ou=system");
            System.out.println("Success");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteUserFromGroup(String username,String groupName){
        ModificationItem[] mods = new ModificationItem[1];
        Attribute attribute = new BasicAttribute("uniqueMember","cn="+username+",ou=users,ou=system");
        mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,attribute);

        try {
            connection.modifyAttributes("cn="+groupName+",ou=groups,ou=system",mods);
            System.out.println("Success");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

    }

    public void searchUser() throws NamingException {
        String searchFilter = "(|(uid=1)(uid=2)(cn=vaghani))";
//        String searchFilter = "(&(uid=1)(cn=dip))";
        String[] reqAtt = {"cn","sn","uid"};
        SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        controls.setReturningAttributes(reqAtt);

        NamingEnumeration users = connection.search("ou=users,ou=system",searchFilter,controls);

        SearchResult result = null;
        while (users.hasMore()){
            result = (SearchResult) users.next();
            Attributes attr = result.getAttributes();

//            String name = attr.get("cn").get(0).toString();
////            addUserToGroup(name,"Administrators");
//
//            deleteUserFromGroup(name,"Administrators");

            System.out.println(attr.get("cn"));
            System.out.println(attr.get("sn"));
            System.out.println(attr.get("uid"));
        }
    }
    public static boolean authUser(String username,String password){
        try{
            Properties env = new Properties();
            env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL,"ldap://localhost:10389");
            env.put(Context.SECURITY_PRINCIPAL,"cn="+username+",ou=users,ou=system");
            env.put(Context.SECURITY_CREDENTIALS,password);
            DirContext context = new InitialDirContext(env);
            context.close();
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void updateUserPassword(String username,String password){
        try {
            String dnBase = ",ou=users,ou=system";
            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("userPassword",password));
            connection.modifyAttributes("cn="+username+dnBase,mods);
            System.out.println("success");
        }catch (Exception e){
            System.out.println("failed"+e.getMessage());
        }
    }

    public void updateUserDetails(String username,String employeeNumber){
        try {
            String dnBase = ",ou=users,ou=system";
            Attribute attribute=new BasicAttribute("employeeNumber",employeeNumber);
            ModificationItem[] mods = new ModificationItem[1];
            mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attribute);
            connection.modifyAttributes("cn="+username+dnBase,mods);
            System.out.println("success");
        }catch (Exception e){
            System.out.println("failed"+e.getMessage());
        }
    }

    public static void main(String[] args) throws NamingException {
        Main main = new Main();
        main.newConnection();
//        main.addUser();
//        main.deleteUser();
        main.getAllUsers();
//        main.searchUser();
//        System.out.println(authUser("dip","Password"));
//        main.updateUserPassword("dip","PASSWORD");
//        main.updateUserDetails("dip","190010116051");
    }
}