
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.Statement;
import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class GUI {

    private ChatClients client;
    private ChatServerInt server;
    
    public void doConnect() {
         if (connect.getText().equals("Connect")) {
            if (username1.getText().length() < 2) {
                JOptionPane.showMessageDialog(frame, "You need to type a name.");
                return;
            }
            if (IPaddress.getText().length() < 2) {
                JOptionPane.showMessageDialog(frame, "You need to type an IP.");
                return;
            }
            String dbURL = "jdbc:mysql://localhost:3306/users?autoReconnect=true&useSSL=false";
            String name = username1.getText();
            String pass = password.getText();

            try (Connection conn = (Connection) DriverManager.getConnection(dbURL, "root", "")) {
                String sql = "SELECT username, password FROM users.signup";
                Statement statement = (Statement) conn.createStatement();
                ResultSet result = statement.executeQuery(sql);

                while (result.next()) {
                    String username = result.getString("username");
                    String password = result.getString("password");

                    if (name.equals(username) && pass.equals(password)) {
                        try {
                            client = new ChatClients(username1.getText());
                            client.setGUI(this);
                            server = (ChatServerInt) Naming.lookup("rmi://" + IPaddress.getText() + "/myabc");
                            server.login(client);
                            updateUsers(server.getConnected());
                            connect.setText("Disconnect");
                        } catch (MalformedURLException | NotBoundException | RemoteException e) {
                            JOptionPane.showMessageDialog(frame, "ERROR, we wouldn't connect....");
                        }
                    }

                }

                // Database
            } catch (Exception e) {

            }
        } else {
            updateUsers(null);
            connect.setText("Connect");
            // Better to implement Logout ....
        }
    }
    
    public void sendText() {
        if (connect.getText().equals("Connect")) {
            JOptionPane.showMessageDialog(frame, "You need to connect first.");
            return;
        }
        String st = tf.getText();
        st = "[" + username1.getText() + "] " + st;
        tf.setText("");
        // Remove if you are going to implement for remote invocation
        try {
            server.publish(st);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void writeMsg(String st) {
        ChatArea.setText(ChatArea.getText() + "\n" + st);
    }
    
    public void updateUsers(Vector v) {
        DefaultListModel listModel = new DefaultListModel();
        if (v != null) {
            for (int i = 0; i < v.size(); i++) {
                try {
                    String tmp = ((ChatClientInt) v.get(i)).getName();
                    listModel.addElement(tmp);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        lst.setModel(listModel);
    }
    
    public static void main(String[] args) {
        System.out.println("Hello World !");
        GUI c = new GUI();
    }

    // User Interface code.
    public GUI() {
        frame = new JFrame("Group Chat");
        JPanel main = new JPanel();
        main.setBackground(Color.CYAN);
        JPanel top = new JPanel();
        JPanel cn = new JPanel();
        JPanel bottom = new JPanel();
        IPaddress = new JTextField();
        tf = new JTextField();
        username1 = new JTextField();
        ChatArea = new JTextArea();
        connect = new JButton("Connect");
        JButton bt = new JButton("Send");
        lst = new JList();
        top.setLayout(new GridLayout(1, 0, 5, 5));
        cn.setLayout(new BorderLayout(5, 5));
        top.add(new JLabel("username:"));
        top.add(username1);
        top.add(new JLabel("IP/Address:"));
        top.add(IPaddress);
        top.add(connect);
        cn.add(new JScrollPane(ChatArea), BorderLayout.CENTER);
        cn.add(lst, BorderLayout.EAST);
        GroupLayout gl_bottom = new GroupLayout(bottom);
        gl_bottom.setHorizontalGroup(gl_bottom.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_bottom.createSequentialGroup()
                        .addComponent(tf, GroupLayout.PREFERRED_SIZE, 403, GroupLayout.PREFERRED_SIZE).addGap(18)
                        .addComponent(bt, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE).addGap(12)));
        gl_bottom.setVerticalGroup(gl_bottom.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_bottom.createSequentialGroup()
                        .addGroup(gl_bottom.createParallelGroup(Alignment.BASELINE)
                                .addComponent(tf, GroupLayout.PREFERRED_SIZE, 76, GroupLayout.PREFERRED_SIZE)
                                .addComponent(bt, GroupLayout.PREFERRED_SIZE, 61, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        bottom.setLayout(gl_bottom);
        main.setBorder(new EmptyBorder(10, 10, 10, 10));
        // Events
        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doConnect();
            }
        });
        bt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendText();
            }
        });
        tf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendText();
            }
        });
        
        frame.setContentPane(main);
        
        JLabel lblPassword = new JLabel("password:");
        
        password = new JPasswordField();
        
        JCheckBox showPassword = new JCheckBox("show password");
        showPassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (showPassword.isSelected()) {
                    password.setEchoChar((char)0);
                }else {
                    password.setEchoChar('*');
                }
            }
        });
        GroupLayout gl_main = new GroupLayout(main);
        gl_main.setHorizontalGroup(
        	gl_main.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_main.createSequentialGroup()
        			.addGroup(gl_main.createParallelGroup(Alignment.LEADING)
        				.addComponent(top, GroupLayout.PREFERRED_SIZE, 518, GroupLayout.PREFERRED_SIZE)
        				.addComponent(cn, GroupLayout.PREFERRED_SIZE, 518, GroupLayout.PREFERRED_SIZE)
        				.addGroup(gl_main.createSequentialGroup()
        					.addComponent(lblPassword)
        					.addGap(55)
        					.addComponent(password, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addComponent(showPassword))
        				.addComponent(bottom, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addContainerGap(25, Short.MAX_VALUE))
        );
        gl_main.setVerticalGroup(
        	gl_main.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_main.createSequentialGroup()
        			.addComponent(top, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_main.createParallelGroup(Alignment.BASELINE)
        				.addComponent(lblPassword)
        				.addComponent(password, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(showPassword))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(cn, GroupLayout.PREFERRED_SIZE, 349, GroupLayout.PREFERRED_SIZE)
        			.addGap(5)
        			.addComponent(bottom, GroupLayout.PREFERRED_SIZE, 76, Short.MAX_VALUE)
        			.addContainerGap())
        );
        main.setLayout(gl_main);
        frame.setSize(609, 543);
        frame.setVisible(true);
    }
    
    JTextArea ChatArea;
    JTextField tf, IPaddress, username1;
    JButton connect;
    JList lst;
    JFrame frame;
    private JPasswordField password;
}
