import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.util.*;
import javax.swing.GroupLayout.Alignment;

public class GUI {
	private ChatClient client;
	private ChatServerInt server;

	public void doConnect() {
		if (connect.getText().equals("Connect")) {
			if (username.getText().length() < 2) {
				JOptionPane.showMessageDialog(frame, "You need to type a name.");
				return;
			}
			if (IPaddress.getText().length() < 2) {
				JOptionPane.showMessageDialog(frame, "You need to type an IP.");
				return;
			}
			try {
				client = new ChatClient(username.getText());
				client.setGUI(this);
				server = (ChatServerInt) Naming.lookup("rmi://" + IPaddress.getText() + "/myabc");
				server.login(client);
				updateUsers(server.getConnected());
				connect.setText("Disconnect");
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "ERROR, we wouldn't connect....");
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
		st = "[" + username.getText() + "] " + st;
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
		if (v != null)
			for (int i = 0; i < v.size(); i++) {
				try {
					String tmp = ((ChatClientInt) v.get(i)).getName();
					listModel.addElement(tmp);
				} catch (Exception e) {
					e.printStackTrace();
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
		username = new JTextField();
		ChatArea = new JTextArea();
		connect = new JButton("Connect");
		JButton bt = new JButton("Send");
		lst = new JList();
		main.setLayout(new BorderLayout(5, 5));
		top.setLayout(new GridLayout(1, 0, 5, 5));
		cn.setLayout(new BorderLayout(5, 5));
		top.add(new JLabel("username:"));
		top.add(username);
		top.add(new JLabel("Group Address: "));
		top.add(IPaddress);
		top.add(connect);
		cn.add(new JScrollPane(ChatArea), BorderLayout.CENTER);
		cn.add(lst, BorderLayout.EAST);
		main.add(top, BorderLayout.NORTH);
		main.add(cn, BorderLayout.CENTER);
		main.add(bottom, BorderLayout.SOUTH);
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
		frame.setSize(554, 528);
		frame.setVisible(true);
	}

	JTextArea ChatArea;
	JTextField tf, IPaddress, username;
	JButton connect;
	JList lst;
	JFrame frame;
}
